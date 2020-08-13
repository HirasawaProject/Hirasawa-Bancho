package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.user.BanchoUser

class FellowSpectatorJoined(spectator: BanchoUser): BanchoPacket(BanchoPacketType.BANCHO_FELLOW_SPECTATOR_JOINED) {
    init {
        writer.writeInt(spectator.id)
    }
}