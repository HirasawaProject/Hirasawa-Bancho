package io.hirasawa.server.bancho.packethandler.multiplayer

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.handler.MultiplayerMatchHandler
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.multiplayer.MatchJoinFailPacket
import io.hirasawa.server.bancho.packets.multiplayer.MatchJoinSuccessPacket
import io.hirasawa.server.bancho.user.BanchoUser

class MatchCreatePacket: PacketHandler(BanchoPacketType.OSU_MATCH_CREATE) {
    override fun handle(reader: OsuReader, writer: OsuWriter, user: BanchoUser) {
        val match = MultiplayerMatchHandler(reader).match
        match.setHost(user)

        Hirasawa.multiplayer.addMatch(match)
        if (match.addToMatch(user)) {
            user.sendPacket(MatchJoinSuccessPacket(match))
        } else {
            // Can't imagine this would happen but just in case
            user.sendPacket(MatchJoinFailPacket())
        }
    }
}