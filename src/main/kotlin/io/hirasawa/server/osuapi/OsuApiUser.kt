package io.hirasawa.server.osuapi

data class OsuApiUser(
    val userId: Int,
    val username: String,
    val joinDate: String,
    val count300: Int,
    val count100: Int,
    val count50: Int,
    val playcount: Int,
    val rankedScore: Long,
    val totalScore: Long,
    val ppRank: Int,
    val level: Float,
    val ppRaw: Float,
    val accuracy: Float,
    val countRankSs: Int,
    val countRankSsh: Int,
    val countRankS: Int,
    val countRankSh: Int,
    val countRankA: Int,
    val country: String,
    val totalSecondsPlayed: Long,
    val ppCountryRank: Int,
    val events: ArrayList<OsuApiEvent>
)