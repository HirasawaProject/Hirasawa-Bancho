package io.hirasawa.server.handlers

import io.hirasawa.server.objects.Score
import java.io.DataOutputStream

class ScoreInfoHandler(val score: Score, val rank: Int, val isLeaderboard: Boolean) {
    fun write(outputStream: DataOutputStream) {
        fun Boolean.toInt() = if (this) 1 else 0
        outputStream.writeBytes("${score.id}|${score.user.username}|${score.score}|${score.combo}|${score.count50}|")
        outputStream.writeBytes("${score.count100}|${score.count300}|${score.countMiss}|${score.countKatu}|")
        outputStream.writeBytes("${score.countGeki}|${score.fullCombo.toInt()}|${score.mods}|${score.user.id}|$rank|")
        outputStream.writeBytes("${score.timestamp}|${isLeaderboard.toInt()}\n")
    }
}