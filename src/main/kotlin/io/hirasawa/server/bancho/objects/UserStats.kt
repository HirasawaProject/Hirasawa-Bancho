package io.hirasawa.server.bancho.objects

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.database.tables.UserStatsTable
import org.jetbrains.exposed.sql.ResultRow

data class UserStats(val userId: Int, var rankedScore: Long, var accuracy: Float, var playcount: Int,
                     var totalScore: Long, var rank: Int, var pp: Short, val gameMode: GameMode) {
    constructor(userId: Int): this(userId, 0, 0F, 0, 0, 0, 0, GameMode.OSU)
    constructor(result: ResultRow): this(result[UserStatsTable.userId], result[UserStatsTable.rankedScore],
        result[UserStatsTable.accuracy], result[UserStatsTable.playcount], result[UserStatsTable.totalScore],
        result[UserStatsTable.rank], result[UserStatsTable.pp], GameMode.values()[result[UserStatsTable.gamemode]])
}