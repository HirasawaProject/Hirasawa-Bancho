package io.hirasawa.server.bancho.handler

import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.objects.ReplayFrame
import io.hirasawa.server.bancho.objects.ScoreFrame
import io.hirasawa.server.objects.Score
import java.io.ByteArrayInputStream
import java.io.DataInputStream

class ScoreFrameHandler(reader: OsuReader) {
    val scoreFrame: ScoreFrame

    init {
        val time = reader.readInt()
        val id = reader.readByte()
        val count300 = reader.readShort()
        val count100 = reader.readShort()
        val count50 = reader.readShort()
        val countGeki = reader.readShort()
        val countKatu = reader.readShort()
        val countMiss = reader.readShort()
        val totalScore = reader.readInt()
        val maxCombo = reader.readShort()
        val currentCombo = reader.readShort()
        val perfect = reader.readBoolean()
        val currentHp = reader.readByte()
        val tagByte = reader.readByte()
        val usingScoreV2 = reader.readBoolean()
        val comboPortion: Double = if (usingScoreV2) reader.readDouble() else 0.0
        val bonusPortion: Double = if (usingScoreV2) reader.readDouble() else 0.0

        scoreFrame = ScoreFrame(time, id, count300, count100, count50, countGeki, countKatu, countMiss, totalScore,
            maxCombo, currentCombo, perfect, currentHp, tagByte, usingScoreV2, comboPortion, bonusPortion)
    }
}