package io.hirasawa.server.osuapi

data class OsuApiGame (
    val gameId: Int,
    val startTime: String,
    val endTime: String,
    val beatmapId: Int,
    val playMode: Int,
    val matchType: Int,
    val scoringType: Int,
    val teamType: Int,
    val mods: Int,
    val scores: ArrayList<OsuApiMultiplayerScore>
)