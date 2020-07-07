package io.hirasawa.server.osuapi

data class OsuApiMatch (
    val match: OsuApiMatchDetails,
    val games: ArrayList<OsuApiGame>
)