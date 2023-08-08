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
import io.hirasawa.server.bancho.user.BanchoBot
import io.hirasawa.server.config.ChatChannelSerialiser
import io.hirasawa.server.config.HirasawaConfig
import io.hirasawa.server.config.ModsSerialiser
import io.hirasawa.server.database.tables.*
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.enums.DefaultRankingState
import io.hirasawa.server.irc.IrcServer
import io.hirasawa.server.irc.clientcommands.IrcProtocolReply
import io.hirasawa.server.lookupmaps.BeatmapLookupMap
import io.hirasawa.server.lookupmaps.BeatmapSetLookupMap
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

        val beatmaps = BeatmapLookupMap()
        val beatmapSets = BeatmapSetLookupMap()

        lateinit var banchoBot: BanchoBot
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
                            it[UsersTable.username] = "BanchoBot"
                            it[UsersTable.password] = ""
                            it[UsersTable.isBanned] = false
                            it[UsersTable.mutedUntil] = 0
                        }
                    }
                }
            } else {
                val dbc = Hirasawa.config.database
                Database.connect("jdbc:mariadb://${dbc.host}/${dbc.database}",
                    driver = "org.mariadb.jdbc.Driver", user = dbc.username, password = dbc.password)
            }


            val permissionGroups = HashMap<String, PermissionGroup>()

            transaction {
                PermissionGroupsTable.selectAll().forEach {
                    val group = PermissionGroup(it)
                    permissionGroups[group.name] = group
                }
            }

            this.permissionEngine = PermissionEngine(permissionGroups)

            val banchoBotUser = BanchoUser(transaction {
                UsersTable.select {
                    UsersTable.id eq Hirasawa.config.banchoBotId
                }.firstOrNull() ?: throw(Exception("User not found"))
            })

            this.banchoBot = BanchoBot(banchoBotUser)
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
            val hashText = number.toString(16).lowercase()

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

        fun rankBeatmapSet(beatmapSetId: Int, rankingState: DefaultRankingState = config.defaultRankingState): Boolean {
            if (transaction {
                    BeatmapSetsTable.select {
                        BeatmapSetsTable.osuId eq beatmapSetId
                    }.count()
                } > 0) {
                return false
            }

            val beatmaps = osuApi.getBeatmaps(mapsetId = beatmapSetId)
            if (beatmaps.isEmpty()) {
                return false
            }

            if (rankingState == DefaultRankingState.NOT_SUBMITTED || rankingState == DefaultRankingState.UNKNOWN) {
                return false
            }

            transaction {
                val osuBeatmapStatus = BeatmapStatus.fromId(beatmaps.first().approved)
                val id = BeatmapSetsTable.insertAndGetId {
                    it[BeatmapSetsTable.artist] = beatmaps.first().artist
                    it[BeatmapSetsTable.title] = beatmaps.first().title
                    it[BeatmapSetsTable.status] = when (rankingState) {
                        DefaultRankingState.RANK_ALL -> {
                            if (osuBeatmapStatus == BeatmapStatus.LOVED) {
                                BeatmapStatus.LOVED
                            } else {
                                BeatmapStatus.RANKED
                            }
                        }
                        DefaultRankingState.MATCH_OSU -> osuBeatmapStatus
                        DefaultRankingState.LOVE_UNRANKED -> {
                            if (osuBeatmapStatus == BeatmapStatus.PENDING) {
                                BeatmapStatus.LOVED
                            } else{
                                osuBeatmapStatus
                            }
                        }
                        else -> BeatmapStatus.NOT_SUBMITTED
                    }.ordinal
                    it[BeatmapSetsTable.osuId] = beatmaps.first().beatmapsetId
                    it[BeatmapSetsTable.mapperName] = beatmaps.first().creator
                    it[BeatmapSetsTable.genreId] = beatmaps.first().genreId
                    it[BeatmapSetsTable.languageId] = beatmaps.first().languageId
                    it[BeatmapSetsTable.rating] = beatmaps.first().rating
                }

                for (beatmap in beatmaps) {
                    BeatmapsTable.insert {
                        it[BeatmapsTable.mapsetId] = id.value
                        it[BeatmapsTable.difficulty] = beatmap.version
                        it[BeatmapsTable.hash] = beatmap.fileMd5
                        it[BeatmapsTable.ranks] = 0
                        it[BeatmapsTable.offset] = 0F
                        it[BeatmapsTable.osuId] = beatmap.beatmapId
                        it[BeatmapsTable.totalLength] = beatmap.totalLength
                        it[BeatmapsTable.hitLength] = beatmap.hitLength
                        it[BeatmapsTable.circleSize] = beatmap.diffSize
                        it[BeatmapsTable.overallDifficulty] = beatmap.diffOverall
                        it[BeatmapsTable.approachRate] = beatmap.diffApproach
                        it[BeatmapsTable.healthDrain] = beatmap.diffDrain
                        it[BeatmapsTable.gamemode] = beatmap.mode
                        it[BeatmapsTable.countNormal] = beatmap.countNormal
                        it[BeatmapsTable.countSlider] = beatmap.countSlider
                        it[BeatmapsTable.countSpinner] = beatmap.countSpinner
                        it[BeatmapsTable.bpm] = beatmap.bpm
                        it[BeatmapsTable.hasStoryboard] = beatmap.storyboard
                        it[BeatmapsTable.maxCombo] = beatmap.maxCombo
                        it[BeatmapsTable.playCount] = 0
                        it[BeatmapsTable.passCount] = 0
                    }

                }
            }

            return true

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