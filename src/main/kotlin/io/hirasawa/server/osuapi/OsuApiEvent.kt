package io.hirasawa.server.osuapi

data class OsuApiEvent(
    val displayHtml: String,
    val beatmapId: Int,
    val beatmapsetId: Int,
    val date: String,
    val epicfactor: Int
)
