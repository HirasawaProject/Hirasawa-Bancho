package io.hirasawa.server.osuapi

data class OsuApiMatchDetails (
    val matchId: Int,
    val name: String,
    val startTime: String,
    val endTime: String
)