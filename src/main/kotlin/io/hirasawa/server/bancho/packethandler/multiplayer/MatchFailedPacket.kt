package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.multiplayer.MatchPlayerFailedPacket
import io.hirasawa.server.bancho.user.BanchoUser

class MatchFailedPacket: PacketHandler(BanchoPacketType.OSU_MATCH_FAILED) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        user.currentMatch?.sendPacketToAll(MatchPlayerFailedPacket(user))
    }
}