package io.hirasawa.server.bancho.packets

class LoginReplyPacket(private val id: Int): BanchoPacket(BanchoPacketType.BANCHO_LOGIN_REPLY) {
    init {
        writer.writeInt(id)
    }
}