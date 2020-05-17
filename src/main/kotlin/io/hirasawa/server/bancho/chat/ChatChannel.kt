package io.hirasawa.server.bancho.chat

import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.user.BanchoUser

data class ChatChannel(val name: String, val description: String, val autojoin: Boolean) {
    private val connectedUsers = ArrayList<BanchoUser>()
    val size get() = connectedUsers.size.toShort()

    fun sendPacketToAll(banchoPacket: BanchoPacket) {
        for (user in connectedUsers) {
            user.sendPacket(banchoPacket)
        }
    }

    fun addUser(banchoUser: BanchoUser) {
        connectedUsers.add(banchoUser)
    }

    fun removePlayer(banchoUser: BanchoUser) {
        connectedUsers.remove(banchoUser)
    }

}