package io.hirasawa.server.bancho.objects

import io.hirasawa.server.bancho.enums.GameMode

data class UserStats(val userId: Int, var rankedScore: Long, var accuracy: Float, var playcount: Int,
                     var totalScore: Long, var rank: Int, var pp: Short, val gameMode: GameMode) {
    constructor(userId: Int): this(userId, 0, 0F, 0, 0, 0, 0, GameMode.OSU)
}