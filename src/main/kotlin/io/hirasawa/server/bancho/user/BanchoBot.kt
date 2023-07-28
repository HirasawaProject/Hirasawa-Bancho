package io.hirasawa.server.bancho.user

import io.hirasawa.server.bancho.packets.BanchoPacket
import java.util.*

class BanchoBot(user: User): BanchoUser(user.id, user.username, user.timezone, user.countryCode, user.longitude,
    user.latitude, UUID.randomUUID(), false) {
    init {
        lastKeepAlive = Int.MAX_VALUE
    }


    override fun sendPacket(banchoPacket: BanchoPacket) {}
}