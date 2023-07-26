package io.hirasawa.server

import com.google.gson.GsonBuilder
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.ChatEngine
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.multiplayer.MultiplayerManager
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.HirasawaBot
import io.hirasawa.server.config.ChatChannelSerialiser
import io.hirasawa.server.config.HirasawaConfig
import io.hirasawa.server.config.ModsSerialiser
import io.hirasawa.server.database.tables.*
import io.hirasawa.server.irc.IrcServer
import io.hirasawa.server.irc.clientcommands.IrcProtocolReply
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.Mods
import io.hirasawa.server.objects.Score
import io.hirasawa.server.objects.UserMap
import io.hirasawa.server.osuapi.OsuApi
import io.hirasawa.server.permissions.PermissionEngine
import io.hirasawa.server.permissions.PermissionGroup
import io.hirasawa.server.pipeline.PipelineManager
import io.hirasawa.server.plugin.PluginManager
import io.hirasawa.server.plugin.event.EventManager
import io.hirasawa.server.update.SemVer
import io.hirasawa.server.update.UpdateChecker
import io.hirasawa.server.webserver.Webserver
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

class Hirasawa {
    companion object {
        private val gson = GsonBuilder()
            .registerTypeAdapter(ChatChannel::class.java, ChatChannelSerialiser())
            .registerTypeAdapter(Mods::class.java, ModsSerialiser())
            .setPrettyPrinting()
            .create()
        val config = loadConfig()
        val webserver = Webserver(config.httpPort, config.httpsPort)
        val eventHandler = EventManager()
        val packetRouter = HashMap<BanchoPacketType, PacketHandler>()
        val pluginManager = PluginManager()
        val chatEngine = ChatEngine()
        val pipeline = PipelineManager()
        val irc = IrcServer(config.ircPort)
        val osuApi = OsuApi(config.osuApiKey)
        val multiplayer = MultiplayerManager()
        val version = SemVer.parse(Hirasawa::class.java.`package`.implementationVersion ?: "0.0.0-noversion+noupdate")
        lateinit var permissionEngine: PermissionEngine
        val updateChecker = UpdateChecker()
        val isUpdateRequired get() = updateChecker.checkUpdate()

        lateinit var hirasawaBot: HirasawaBot
        val banchoUsers = UserMap<BanchoUser>()

        /**
         * Sends a BanchoPacket to all connected users on Bancho
         *
         * @param banchoPacket The packet to send
         */
        fun sendBanchoPacketToAll(banchoPacket: BanchoPacket) {
            for (user in banchoUsers) {
                user.sendPacket(banchoPacket)
            }
        }

        /**
         * Send IRC reply to all connected users over IRC
         */
        fun sendIrcReplyToAll(ircProtocolReply: IrcProtocolReply) {
            for (user in irc.connectedUsers) {
                user.sendReply(ircProtocolReply)
            }
        }

        fun initDatabase(memoryDatabase: Boolean = false) {
            if (memoryDatabase) {
                Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
                // TODO switch transaction with SOON^TM database migration system
                transaction {
                    SchemaUtils.create(BeatmapSetsTable, BeatmapsTable, FriendsTable, PermissionGroupsTable,
                        PermissionGroupUsersTable, ScoresTable, UsersTable, UserStatsTable, PermissionNodesTable)

                    if (UsersTable.select { UsersTable.id eq Hirasawa.config.banchoBotId }.count() == 0L) {
                        UsersTable.insert {
                            it[UsersTable.id] = EntityID<Int>(config.banchoBotId, UsersTable)
                            it[UsersTable.username] = "HirasawaBot"
                            it[UsersTable.password] = ""
                            it[UsersTable.isBanned] = false
                            it[UsersTable.mutedUntil] = 0
                        }
                    }
                }
            } else {
                val dbc = Hirasawa.config.database
                Database.connect("jdbc:mysql://${dbc.host}/${dbc.database}",
                    driver = "com.mysql.jdbc.Driver", user = dbc.username, password = dbc.password)
            }


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
                }.firstOrNull() ?: throw(Exception("User not found"))
            })

            this.hirasawaBot = HirasawaBot(hirasawaBotUser)
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
                UsersTable.select { UsersTable.username eq username }.firstOrNull()
            } ?: return false

            return BCrypt.checkpw(password, result[UsersTable.password])
        }

        fun authenticateIrc(username: String, ircToken: String): Boolean {
            return transaction {
                UsersTable.select { UsersTable.username eq username and (UsersTable.ircToken eq ircToken) }.empty().not()
            }
        }

        // TODO move out
        fun processLeaderboard(beatmap: Beatmap, gameMode: GameMode) {
            var rank = 0

            transaction {
                (ScoresTable innerJoin UsersTable).select {
                    (ScoresTable.beatmapId eq beatmap.id) and (ScoresTable.gamemode eq gameMode.ordinal)
                }.forEach {
                    val score = Score(it)
                    if (!score.user.isBanned) {
                        score.rank = ++rank
                        transaction {
                            ScoresTable.update({ ScoresTable.id eq score.id }) {
                                it[ScoresTable.rank] = score.rank
                            }
                        }
                    }
                }
            }

            transaction {
                BeatmapsTable.update({ BeatmapsTable.id eq beatmap.id }) {
                    it[ranks] = rank
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

        @Suppress("UNCHECKED_CAST")
        fun <T> databaseToObject(dataType: KClass<*>, resultRow: ResultRow?): T? {
            if (resultRow == null) {
                return null
            }
            for (constructor in dataType.constructors) {
                if (constructor.parameters.first().type == ResultRow::class.createType()) {
                    val instance = constructor.call(resultRow)
                    if (instance::class.createType() == dataType.createType()) {
                        return instance as T
                    } else {
                        return null
                    }

                }
            }

            return null
        }
    }
}