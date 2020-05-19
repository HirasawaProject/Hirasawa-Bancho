package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.serialisation.BanchoIntListWriter

class UserPesenceSinglePacket(id: Int):
        BanchoPacket(BanchoPacketType.BANCHO_USER_PRESENCE_SINGLE) {
    init {
        writer.writeInt(id)
    }
}