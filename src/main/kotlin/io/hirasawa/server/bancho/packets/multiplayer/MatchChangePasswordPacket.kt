package io.hirasawa.server.bancho.packets.multiplayer

import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.BanchoPacketType

class MatchChangePasswordPacket(password: String): BanchoPacket(BanchoPacketType.BANCHO_MATCH_CHANGE_PASSWORD) {
    init {
        writer.writeString(password)
    }
}