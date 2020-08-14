package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.user.BanchoUser

class SpectatorLeft(spectator: BanchoUser): BanchoPacket(BanchoPacketType.BANCHO_SPECTATOR_LEFT) {
    init {
        writer.writeInt(spectator.id)
    }
}