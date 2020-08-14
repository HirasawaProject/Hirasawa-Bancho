package io.hirasawa.server.bancho.objects

data class ScoreFrame (var time: Int, var id: Byte, var count300: Short, var count100: Short, var count50: Short, 
                       var countGeki: Short, var countKatu: Short, var countMiss: Short, var totalScore: Int,
                       var maxCombo: Short, var currentCombo: Short, var perfect: Boolean, var currentHp: Byte,
                       var tagByte: Byte, var usingScoreV2: Boolean, var comboPortion: Double, var bonusPortion: Double)