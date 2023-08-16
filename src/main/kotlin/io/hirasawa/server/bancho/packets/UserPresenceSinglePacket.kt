package io.hirasawa.server.bancho.packets

class UserPresenceSinglePacket(id: Int):
        BanchoPacket(BanchoPacketType.BANCHO_USER_PRESENCE_SINGLE) {
    init {
        writer.writeInt(id)
    }
}