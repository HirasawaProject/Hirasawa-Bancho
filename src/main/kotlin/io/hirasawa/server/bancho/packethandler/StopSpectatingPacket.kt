package io.hirasawa.server.bancho.packethandler

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.*
import io.hirasawa.server.bancho.user.BanchoUser

class StopSpectatingPacket: PacketHandler(BanchoPacketType.OSU_START_SPECTATING) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val spectatingUser = user.spectating ?: return

        spectatingUser.spectators.remove(user)
        user.spectating = null

        for (spectators in spectatingUser.spectators) {
            spectators.sendPacket(FellowSpectatorLeft(user))
        }

        spectatingUser.sendPacket(SpectatorLeft(user))
        user.sendPacket(ChannelRevokedPacket(Hirasawa.chatEngine.spectatorChannel))
    }
}