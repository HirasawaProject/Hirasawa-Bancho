package io.hirasawa.server.bancho.packets

import io.hirasawa.server.plugin.event.bancho.enums.BanchoLoginCancelReason

class LoginReplyPacket(private val id: Int): BanchoPacket(BanchoPacketType.BANCHO_LOGIN_REPLY) {
    constructor(reason: BanchoLoginCancelReason): this(reason.id)
    init {
        writer.writeInt(id)
    }
}