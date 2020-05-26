package io.hirasawa.server.bancho.objects

import io.hirasawa.server.bancho.enums.GameMode

data class UserStats(val userId: Int, val rankedScore: Long, val accuracy: Float, val playcount: Int,
                     val totalScore: Long, val rank: Int, val pp: Short, val gameMode: GameMode) {
    constructor(userId: Int): this(userId, 0, 0F, 0, 0, 0, 0, GameMode.OSU)
}