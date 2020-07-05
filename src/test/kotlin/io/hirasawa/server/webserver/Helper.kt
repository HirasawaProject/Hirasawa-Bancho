package io.hirasawa.server.webserver

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.database.tables.*
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.objects.Score
import io.hirasawa.server.permissions.PermissionGroup
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class Helper {
    companion object {
        fun createUser(username: String): User {
            return BanchoUser(transaction {
                val userId = UsersTable.insertAndGetId {
                    it[UsersTable.username] = username
                    it[UsersTable.password] = BCrypt.hashpw("", BCrypt.gensalt())
                    it[UsersTable.banned] = false
                    it[UsersTable.mutedUntil] = 0
                }

                UsersTable.select { UsersTable.id eq userId }.first()
            })
        }

        fun createScore(user: User, score: Int, rank: Int, beatmapId: Int, gamemode: GameMode): Score {
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
                    it[ScoresTable.timestamp] = 0
                    it[ScoresTable.beatmapId] = beatmapId
                    it[ScoresTable.gamemode] = gamemode.ordinal
                    it[ScoresTable.rank] = rank
                    it[ScoresTable.accuracy] = 0F
                }

                (ScoresTable innerJoin UsersTable).select { ScoresTable.id eq scoreId }.first()
            })
        }

        fun createBeatmap(mapsetId: Int, hash: String, ranks: Int): Beatmap {
            return Beatmap(transaction {
                val beatmapId = BeatmapsTable.insertAndGetId {
                    it[BeatmapsTable.mapsetId] = mapsetId
                    it[BeatmapsTable.difficulty] = "HARD"
                    it[BeatmapsTable.hash] = hash
                    it[BeatmapsTable.ranks] = ranks
                    it[BeatmapsTable.offset] = 0F
                    it[BeatmapsTable.osuId] = Random.nextInt()
                    it[BeatmapsTable.totalLength] = 0
                    it[BeatmapsTable.hitLength] = 0
                    it[BeatmapsTable.circleSize] = 0F
                    it[BeatmapsTable.overallDifficulty] = 0F
                    it[BeatmapsTable.approachRate] = 0F
                    it[BeatmapsTable.healthDrain] = 0F
                    it[BeatmapsTable.gamemode] = GameMode.OSU.ordinal
                    it[BeatmapsTable.countNormal] = 0
                    it[BeatmapsTable.countSlider] = 0
                    it[BeatmapsTable.countSpinner] = 0
                    it[BeatmapsTable.bpm] = 0F
                    it[BeatmapsTable.hasStoryboard] = false
                    it[BeatmapsTable.maxCombo] = 0
                    it[BeatmapsTable.playCount] = 0
                    it[BeatmapsTable.passCount] = 0
                }

                BeatmapsTable.select { BeatmapsTable.id eq beatmapId }.first()
            })
        }

        fun createBeatmapSet(artist: String, title: String, status: BeatmapStatus): BeatmapSet {
            return BeatmapSet(transaction {
                val mapsetId = BeatmapsetsTable.insertAndGetId {
                    it[BeatmapsetsTable.artist] = artist
                    it[BeatmapsetsTable.title] = title
                    it[BeatmapsetsTable.status] = status.id
                    it[BeatmapsetsTable.osuId] = Random.nextInt()
                    it[BeatmapsetsTable.mapperName] = "Unknown"
                    it[BeatmapsetsTable.genreId] = 0
                    it[BeatmapsetsTable.languageId] = 0
                    it[BeatmapsetsTable.rating] = 0F
                }

                BeatmapsetsTable.select { BeatmapsetsTable.id eq mapsetId }.first()
            })
        }

        fun createUserStats(userId: Int, gamemode: GameMode): UserStats {
            return UserStats(transaction {
                UserStatsTable.insert {
                    it[UserStatsTable.userId] = userId
                    it[UserStatsTable.rankedScore] = 0
                    it[UserStatsTable.accuracy] = 0F
                    it[UserStatsTable.playcount] = 0
                    it[UserStatsTable.totalScore] = 0
                    it[UserStatsTable.rank] = 0
                    it[UserStatsTable.pp] = 0
                    it[UserStatsTable.gamemode] = gamemode.ordinal
                }

                UserStatsTable.select { (UserStatsTable.userId eq userId) and
                        (UserStatsTable.gamemode eq gamemode.ordinal) }.first()
            })
        }

        fun userToBanchoUser(user: User): BanchoUser {
            return BanchoUser(user.id, user.username, user.timezone, user.countryCode, user.longitude, user.latitude,
                UUID.randomUUID(), user.isBanned)
        }
    }
}