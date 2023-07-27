package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

class MatchLockPacket: PacketHandler(BanchoPacketType.OSU_MATCH_LOCK) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val slot = reader.readInt()

        user.currentMatch?.toggleSlot(slot)
    }
}