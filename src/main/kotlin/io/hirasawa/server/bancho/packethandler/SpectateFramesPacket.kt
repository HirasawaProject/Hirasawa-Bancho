package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.handler.ReplayFrameHandler
import io.hirasawa.server.bancho.handler.ScoreFrameHandler
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.objects.ReplayFrame
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.SpectateFramesPacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.HirasawaBot
import io.hirasawa.server.plugin.event.bancho.BanchoUserSpectateFramesEvent

class SpectateFramesPacket: PacketHandler(BanchoPacketType.OSU_SPECTATE_FRAMES) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val playerId = reader.readInt()

        val size = reader.readShort()

        val replayFrames = ArrayList<ReplayFrame>()
        for (i in 0 until size) {
            replayFrames.add(ReplayFrameHandler(reader).replayFrame)
        }

        val action = reader.readByte()

        val scoreFrame = ScoreFrameHandler(reader).scoreFrame

        val event = BanchoUserSpectateFramesEvent(user, replayFrames, action, scoreFrame)
        Hirasawa.eventHandler.callEvent(event)

        if (event.isCancelled) {
            return
        }

        for (spectator in user.spectators) {
            spectator.sendPacket(SpectateFramesPacket(user.id, event.replayFrames, event.action, event.scoreFrame))
        }
    }
}