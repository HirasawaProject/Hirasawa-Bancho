package io.hirasawa.server.bancho.packets.multiplayer

import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.user.BanchoUser

class MatchPlayerFailedPacket(user: BanchoUser): BanchoPacket(BanchoPacketType.BANCHO_MATCH_PLAYER_FAILED) {
    init {
        writer.writeInt(user.currentMatch?.getUserSlot(user) ?: -1)
    }
}