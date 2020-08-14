package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.user.BanchoUser

class SpectatorJoined(spectator: BanchoUser): BanchoPacket(BanchoPacketType.BANCHO_SPECTATOR_JOINED) {
    init {
        writer.writeInt(spectator.id)
    }
}