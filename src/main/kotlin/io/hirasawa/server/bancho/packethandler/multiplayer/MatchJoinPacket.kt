package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.multiplayer.MatchJoinFailPacket
import io.hirasawa.server.bancho.packets.multiplayer.MatchJoinSuccessPacket
import io.hirasawa.server.bancho.user.BanchoUser

class MatchJoinPacket: PacketHandler(BanchoPacketType.OSU_MATCH_JOIN) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val id = reader.readInt()
        val password = reader.readString()

        val match = Hirasawa.multiplayer.matches[id.toShort()] ?: return
        if (match.doesPasswordMatch(password) && match.addToMatch(user)) {
            user.sendPacket(MatchJoinSuccessPacket(match))
        } else {
            user.sendPacket(MatchJoinFailPacket())
        }
    }
}