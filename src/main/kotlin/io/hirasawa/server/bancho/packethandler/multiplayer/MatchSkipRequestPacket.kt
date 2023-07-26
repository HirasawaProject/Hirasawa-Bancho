package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.objects.Mods

class MatchSkipRequestPacket: PacketHandler(BanchoPacketType.OSU_MATCH_SKIP_REQUEST) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        user.currentMatch?.sendSkipRequest(user)
    }
}