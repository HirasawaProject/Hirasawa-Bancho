package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.bancho.enums.MatchSlotStatus
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

class MatchHasBeatmapPacket: PacketHandler(BanchoPacketType.OSU_MATCH_HAS_BEATMAP) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        user.currentMatch?.setSlotStatus(user, MatchSlotStatus.NOT_READY)
    }
}