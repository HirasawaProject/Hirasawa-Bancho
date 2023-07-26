package io.hirasawa.server.bancho.packets.multiplayer

import io.hirasawa.server.bancho.objects.ScoreFrame
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

class MatchScoreUpdatePacket(user: BanchoUser, scoreFrame: ScoreFrame): BanchoPacket(BanchoPacketType.BANCHO_MATCH_SCORE_UPDATE) {
    init {
        writer.writeInt(user.id)
        writer.writeInt(scoreFrame.time)
        writer.writeByte(scoreFrame.id)
        writer.writeShort(scoreFrame.count300)
        writer.writeShort(scoreFrame.count100)
        writer.writeShort(scoreFrame.count50)
        writer.writeShort(scoreFrame.countGeki)
        writer.writeShort(scoreFrame.countKatu)
        writer.writeShort(scoreFrame.countMiss)
        writer.writeInt(scoreFrame.totalScore)
        writer.writeShort(scoreFrame.maxCombo)
        writer.writeShort(scoreFrame.currentCombo)
        writer.writeBoolean(scoreFrame.perfect)
        writer.writeByte(scoreFrame.currentHp)
        writer.writeByte(scoreFrame.tagByte)
        writer.writeBoolean(scoreFrame.usingScoreV2)
        writer.writeDouble(if (scoreFrame.usingScoreV2) scoreFrame.comboPortion else 0.0)
        writer.writeDouble(if (scoreFrame.usingScoreV2) scoreFrame.bonusPortion else 0.0)
    }
}