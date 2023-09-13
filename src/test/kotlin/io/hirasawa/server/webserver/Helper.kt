package io.hirasawa.server.webserver

import at.favre.lib.crypto.bcrypt.BCrypt
import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.Mode
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.database.tables.*
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.objects.Score
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class Helper {
    companion object {
        fun createUser(username: String, ircToken: String? = "ircToken", permissions: ArrayList<String> = arrayListOf()): User {
            return BanchoUser(transaction {
                val userId = UsersTable.insertAndGetId {
                    it[UsersTable.username] = username
                    it[UsersTable.password] = String(BCrypt.with(BCrypt.Version.VERSION_2Y).hashToChar(10, "".toCharArray()))
                    it[UsersTable.isBanned] = false
                    it[UsersTable.ircToken] = ircToken
                    it[UsersTable.email] = ""
                }

                if (permissions.isNotEmpty()) {
                    val permissionGroupId = PermissionGroupsTable.insertAndGetId {
                        it[PermissionGroupsTable.name] = "$username group"
                    }

                    permissions.forEach { permission ->
                        PermissionNodesTable.insert {
                            it[PermissionNodesTable.node] = permission
                            it[PermissionNodesTable.groupId] = permissionGroupId.value
                        }
                    }

                    PermissionGroupUserTable.insert {
                        it[PermissionGroupUserTable.groupId] = permissionGroupId.value
                        it[PermissionGroupUserTable.userId] = userId.value
                    }

                    Hirasawa.permissionEngine.reload()
                }

                UsersTable.select { UsersTable.id eq userId }.first()
            })
        }

        fun createScore(user: User, score: Int, rank: Int, beatmapId: Int, gamemode: Mode): Score {
            return Score(transaction {
                val scoreId = ScoresTable.insertAndGetId {
                    it[ScoresTable.userId] = user.id
                    it[ScoresTable.score] = score
                    it[ScoresTable.combo] = 0
                    it[ScoresTable.count50] = 50
                    it[ScoresTable.count100] = 100
                    it[ScoresTable.count300] = 300
                    it[ScoresTable.countMiss] = 0
                    it[ScoresTable.countKatu] = 12
                    it[ScoresTable.countGeki] = 11
                    it[ScoresTable.fullCombo] = true
                    it[ScoresTable.mods] = 0
                    it[ScoresTable.beatmapId] = beatmapId
                    it[ScoresTable.mode] = gamemode.ordinal
                    it[ScoresTable.rank] = rank
                    it[ScoresTable.accuracy] = 0F
                }

                (ScoresTable innerJoin UsersTable).select { ScoresTable.id eq scoreId }.first()
            })
        }

        fun createBeatmap(mapsetId: Int, hash: String): Beatmap {
            return Beatmap(transaction {
                val beatmapId = BeatmapsTable.insertAndGetId {
                    it[BeatmapsTable.mapsetId] = mapsetId
                    it[BeatmapsTable.difficultyName] = "HARD"
                    it[BeatmapsTable.hash] = hash
                    it[BeatmapsTable.offset] = 0F
                    it[BeatmapsTable.osuId] = Random.nextInt()
                    it[BeatmapsTable.totalLength] = 0
                    it[BeatmapsTable.hitLength] = 0
                    it[BeatmapsTable.circleSize] = 0F
                    it[BeatmapsTable.overallDifficulty] = 0F
                    it[BeatmapsTable.approachRate] = 0F
                    it[BeatmapsTable.healthDrain] = 0F
                    it[BeatmapsTable.mode] = Mode.OSU.ordinal
                    it[BeatmapsTable.countNormal] = 0
                    it[BeatmapsTable.countSlider] = 0
                    it[BeatmapsTable.countSpinner] = 0
                    it[BeatmapsTable.bpm] = 0F
                    it[BeatmapsTable.hasStoryboard] = false
                    it[BeatmapsTable.maxCombo] = 0
                }

                BeatmapsTable.select { BeatmapsTable.id eq beatmapId }.first()
            })
        }

        fun createBeatmapSet(artist: String, title: String, status: BeatmapStatus): BeatmapSet {
            return BeatmapSet(transaction {
                val mapsetId = BeatmapSetsTable.insertAndGetId {
                    it[BeatmapSetsTable.artist] = artist
                    it[BeatmapSetsTable.title] = title
                    it[BeatmapSetsTable.status] = status.id
                    it[BeatmapSetsTable.osuId] = Random.nextInt()
                    it[BeatmapSetsTable.mapperName] = "Unknown"
                    it[BeatmapSetsTable.genreId] = 0
                    it[BeatmapSetsTable.languageId] = 0
                    it[BeatmapSetsTable.rating] = 0F
                }

                BeatmapSetsTable.select { BeatmapSetsTable.id eq mapsetId }.first()
            })
        }

        fun createUserStats(userId: Int, gamemode: Mode): UserStats {
            return UserStats(transaction {
                UserStatsTable.insert {
                    it[UserStatsTable.userId] = userId
                    it[UserStatsTable.rankedScore] = 0
                    it[UserStatsTable.accuracy] = 0F
                    it[UserStatsTable.playCount] = 0
                    it[UserStatsTable.totalScore] = 0
                    it[UserStatsTable.rank] = 0
                    it[UserStatsTable.pp] = 0
                    it[UserStatsTable.mode] = gamemode.ordinal
                }

                UserStatsTable.select { (UserStatsTable.userId eq userId) and
                        (UserStatsTable.mode eq gamemode.ordinal) }.first()
            })
        }

        fun userToBanchoUser(user: User): BanchoUser {
            return BanchoUser(user.id, user.username, user.timezone, user.countryCode, user.longitude, user.latitude,
                UUID.randomUUID(), user.isBanned)
        }
    }
}