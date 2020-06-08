package io.hirasawa.server.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.User

data class Score(var id: Int, var user: User, var score: Int, var combo: Int, var count50: Int, var count100: Int,
                 var count300: Int, var countMiss: Int, var countKatu: Int, var countGeki: Int, var fullCombo: Boolean,
                 var mods: Int, var timestamp: Int, var gameMode: GameMode, var rank: Int, var beatmapId: Int) {
    val beatmap: Beatmap by lazy {
        Hirasawa.database.getBeatmap(beatmapId)!!
    }
}