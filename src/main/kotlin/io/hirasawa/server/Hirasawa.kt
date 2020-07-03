package io.hirasawa.server

import com.google.gson.GsonBuilder
import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.chat.ChatEngine
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.objects.BanchoUserMap
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.HirasawaBot
import io.hirasawa.server.config.ChatChannelSerialiser
import io.hirasawa.server.config.HirasawaConfig
import io.hirasawa.server.database.tables.*
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.Score
import io.hirasawa.server.permissions.PermissionEngine
import io.hirasawa.server.permissions.PermissionGroup
import io.hirasawa.server.pipeline.PipelineManager
import io.hirasawa.server.plugin.PluginManager
import io.hirasawa.server.plugin.event.EventManager
import io.hirasawa.server.update.UpdateChecker
import io.hirasawa.server.webserver.Webserver
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class Hirasawa {
    companion object {
        private val gson = GsonBuilder()
            .registerTypeAdapter(ChatChannel::class.java, ChatChannelSerialiser())
            .setPrettyPrinting()
            .create()
        val config = loadConfig()
        val webserver = Webserver(config.httpPort, config.httpsPort)
        val eventHandler = EventManager()
        val packetRouter = HashMap<BanchoPacketType, PacketHandler>()
        val pluginManager = PluginManager()
        val chatEngine = ChatEngine()
        val pipeline = PipelineManager()
        val version = Hirasawa::class.java.`package`.implementationVersion ?: "TESTING"
        lateinit var permissionEngine: PermissionEngine
        val updateChecker = UpdateChecker()
        val isUpdateRequired get() = updateChecker.checkUpdate()

        lateinit var hirasawaBot: HirasawaBot
        val banchoUsers = BanchoUserMap()

        fun sendBanchoPacketToAll(banchoPacket: BanchoPacket) {
            for (user in banchoUsers) {
                user.sendPacket(banchoPacket)
            }
        }

        fun initDatabase() {
            val dbc = Hirasawa.config.database
            val db = org.jetbrains.exposed.sql.Database.connect("jdbc:mysql://${dbc.host}/${dbc.database}",
                driver = "com.mysql.jdbc.Driver", user = dbc.username, password = dbc.password)

            val permissionGroups = HashMap<String, PermissionGroup>()

            transaction {
                PermissionGroupsTable.selectAll().forEach {
                    val group = PermissionGroup(it)
                    permissionGroups[group.name] = group
                }
            }

            this.permissionEngine = PermissionEngine(permissionGroups)

            val hirasawaBotUser = BanchoUser(transaction {
                UsersTable.select {
                    UsersTable.id eq Hirasawa.config.banchoBotId
                }.first()
            })

            this.hirasawaBot = HirasawaBot(hirasawaBotUser ?: throw(Exception("User not found")))
        }

        private fun loadConfig(): HirasawaConfig {
            if (File("config.json").exists()) {
                return gson.fromJson(FileReader(File("config.json")), HirasawaConfig::class.java)
            } else {
                val config = HirasawaConfig()
                val writer = FileWriter("config.json")
                gson.toJson(config, writer)
                writer.close()
                return config
            }
        }

        fun authenticateWithMd5(username: String, password: String): Boolean {
            val messageDigest = MessageDigest.getInstance("MD5")
            val bytes = messageDigest.digest(password.toByteArray())
            val number = BigInteger(1, bytes)
            val hashText = number.toString(16).toLowerCase()

            return authenticate(username, hashText)
        }

        fun authenticate(username: String, password: String): Boolean {
            val result = transaction {
                UsersTable.select { UsersTable.username eq username }.first()
            }

            return BCrypt.checkpw(password, result[UsersTable.password])
        }

        // TODO move out
        fun processLeaderboard(beatmap: Beatmap, gameMode: GameMode) {
            var rank = 1

            val scores = transaction {
                ScoresTable.select {
                    (ScoresTable.beatmapId eq beatmap.id) and (ScoresTable.gamemode eq gameMode.ordinal)
                }
            }

            for (scoreData in scores) {
                val score = Score(scoreData)
                if (!score.user.isBanned) {
                    score.rank = rank++
                    transaction {
                        ScoresTable.update({ ScoresTable.id eq score.id }) {
                            it[ScoresTable.rank] = score.rank
                        }
                    }
                }
            }

            transaction {
                BeatmapsTable.update({ BeatmapsTable.id eq beatmap.id }) {
                    it[ranks] = scores.fetchSize!!
                }
            }
        }

        // TODO move out
        fun processGlobalLeaderboard(gameMode: GameMode) {
            var rank = 1
            transaction {
                UserStatsTable.select {
                    UserStatsTable.gamemode eq gameMode.ordinal
                }.sortedByDescending { UserStatsTable.rankedScore }
            }.forEach {
                val userStats = UserStats(it)
                val user = BanchoUser(transaction {
                    UsersTable.select { UsersTable.id eq userStats.userId }
                }.first())
                if (!user.isBanned) {
                    userStats.rank = rank++
                    UserStatsTable.update({ (UserStatsTable.userId eq user.id) and
                            (UserStatsTable.gamemode eq gameMode.ordinal) }) {
                        it[UserStatsTable.rank] = userStats.rank
                    }
                }
            }
        }
    }
}