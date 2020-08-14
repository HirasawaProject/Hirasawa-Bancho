package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.user.BanchoUser

class FellowSpectatorLeft(spectator: BanchoUser): BanchoPacket(BanchoPacketType.BANCHO_FELLOW_SPECTATOR_LEFT) {
    init {
        writer.writeInt(spectator.id)
    }
}