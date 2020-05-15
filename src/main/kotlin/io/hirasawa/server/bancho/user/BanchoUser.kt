package io.hirasawa.server.bancho.user

import io.hirasawa.server.bancho.packets.BanchoPacket
import java.util.*

class BanchoUser(id: Int, username: String) : User(id, username) {
    val packetCache = Stack<BanchoPacket>()

    override fun onMessage(from: User, channel: String, message: String) {
        // TODO send message
    }

    /**
     * Send a packet to the user
     *
     * @param banchoPacket The packet to send
     */
    fun sendPacket(banchoPacket: BanchoPacket) {
        packetCache.push(banchoPacket)
    }


}