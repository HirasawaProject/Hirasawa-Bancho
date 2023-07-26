package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.bancho.handler.ScoreFrameHandler
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

class MatchScoreUpdatePacket: PacketHandler(BanchoPacketType.OSU_MATCH_SCORE_UPDATE) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val scoreFrame = ScoreFrameHandler(reader).scoreFrame
        user.currentMatch?.sendPacketToAll(io.hirasawa.server.bancho.packets.multiplayer.MatchScoreUpdatePacket(user, scoreFrame))
    }
}