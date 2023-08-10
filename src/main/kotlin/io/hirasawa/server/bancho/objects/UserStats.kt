package io.hirasawa.server.bancho.objects

import io.hirasawa.server.bancho.enums.Mode
import io.hirasawa.server.database.tables.UserStatsTable
import org.jetbrains.exposed.sql.ResultRow

data class UserStats(val userId: Int, var rankedScore: Long, var accuracy: Float, var playcount: Int,
                     var totalScore: Long, var rank: Int, var pp: Short, val mode: Mode) {
    constructor(userId: Int): this(userId, 0, 0F, 0, 0, 0, 0, Mode.OSU)
    constructor(result: ResultRow): this(result[UserStatsTable.userId], result[UserStatsTable.rankedScore],
        result[UserStatsTable.accuracy], result[UserStatsTable.playCount], result[UserStatsTable.totalScore],
        result[UserStatsTable.rank], result[UserStatsTable.pp], Mode.values()[result[UserStatsTable.mode]])
}