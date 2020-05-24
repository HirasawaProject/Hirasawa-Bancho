package io.hirasawa.server.objects

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.User

data class Score(val id: Int, val user: User, val score: Int, val combo: Int, val count50: Int, val count100: Int,
                 val count300: Int, val countMiss: Int, val countKatu: Int, val countGeki: Int, val fullCombo: Boolean,
                 val mods: Int, val timestamp: Int, val gameMode: GameMode)