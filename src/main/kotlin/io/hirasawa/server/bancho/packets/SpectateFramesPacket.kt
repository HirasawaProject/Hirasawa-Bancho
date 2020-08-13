package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.objects.ReplayFrame
import io.hirasawa.server.bancho.objects.ScoreFrame

class SpectateFramesPacket(playerId: Int,
                           replayFrames: ArrayList<ReplayFrame>,
                           action: Byte,
                           scoreFrame: ScoreFrame): BanchoPacket(BanchoPacketType.BANCHO_SPECTATE_FRAMES) {
    init {
        writer.writeInt(playerId)
        writer.writeShort(replayFrames.size.toShort())
        for (frame in replayFrames) {
            writer.writeByte(frame.buttonState)
            writer.writeByte(0)
            writer.writeFloat(frame.mouseX)
            writer.writeFloat(frame.mouseY)
            writer.writeInt(frame.timeframe)
        }
        writer.writeByte(action)

        // Score frame
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
        writer.writeByte(scoreFrame.tagByte)
        writer.writeBoolean(scoreFrame.usingScoreV2)
        writer.writeDouble(if (scoreFrame.usingScoreV2) scoreFrame.comboPortion else 0.0)
        writer.writeDouble(if (scoreFrame.usingScoreV2) scoreFrame.bonusPortion else 0.0)
    }
}