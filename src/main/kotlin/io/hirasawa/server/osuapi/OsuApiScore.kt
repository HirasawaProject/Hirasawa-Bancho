package io.hirasawa.server.osuapi

data class OsuApiScore(
    val scoreId: Long,
    val score: Int,
    val username: String,
    val count300: Int,
    val count100: Int,
    val count50: Int,
    val countmiss: Int,
    val maxcombo: Int,
    val countkatu: Int,
    val countgeki: Int,
    val perfect: Boolean,
    val enabledMods: Int,
    val userId: Int,
    val date: String,
    val rank: String,
    val pp: Float,
    val replayAvailable: Boolean
)