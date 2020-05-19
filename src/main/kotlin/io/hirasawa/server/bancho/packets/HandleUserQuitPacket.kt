package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.enums.QuitState
import io.hirasawa.server.bancho.user.BanchoUser

class HandleUserQuitPacket(val user: BanchoUser, val quitState: QuitState): BanchoPacket(BanchoPacketType.BANCHO_HANDLE_USER_QUIT) {
    init {
        writer.writeInt(user.id)
        writer.writeByte(quitState.ordinal.toByte())
    }
}