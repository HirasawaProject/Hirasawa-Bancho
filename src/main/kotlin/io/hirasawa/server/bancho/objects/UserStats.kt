package io.hirasawa.server.bancho.objects

data class UserStats(val userId: Int, val status: BanchoStatus, val rankedScore: Long, val accuracy: Float,
                     val playcount: Int, val totalScore: Long, val rank: Int, val pp: Short)