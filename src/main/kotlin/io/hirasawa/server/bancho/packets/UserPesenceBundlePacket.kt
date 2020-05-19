package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.serialisation.BanchoIntListWriter

class UserPesenceBundlePacket(list: List<Int>):
        BanchoPacket(BanchoPacketType.BANCHO_USER_PRESENCE_BUNDLE) {
    init {
        writer.writeSerialised(BanchoIntListWriter(list))
    }
}