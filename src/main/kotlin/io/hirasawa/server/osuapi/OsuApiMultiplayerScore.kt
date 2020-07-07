package io.hirasawa.server.osuapi

data class OsuApiMultiplayerScore (
    val slot: Int,
    val team: Int,
    val userId: Int,
    val score: Int,
    val maxcombo: Int,
    val rank: Int,
    val count50: Int,
    val count100: Int,
    val count300: Int,
    val countmiss: Int,
    val countgeki: Int,
    val countkatu: Int,
    val perfect: Boolean,
    val pass: Boolean,
    val enabledMods: Int
)