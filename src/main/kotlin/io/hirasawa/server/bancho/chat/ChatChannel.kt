package io.hirasawa.server.bancho.chat

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.message.GlobalChatMessage
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.ChannelAvailablePacket
import io.hirasawa.server.bancho.packets.SendMessagePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User

data class ChatChannel(val name: String, val description: String, val autojoin: Boolean) {
    private val connectedUsers = ArrayList<BanchoUser>()
    val size get() = connectedUsers.size.toShort()

    fun sendPacketToAll(banchoPacket: BanchoPacket) {
        for (user in connectedUsers) {
            user.sendPacket(banchoPacket)
        }
    }

    private fun sendPacketToAllExcluding(banchoPacket: BanchoPacket, exclude: User) {
        for (user in connectedUsers) {
            if (user == exclude) continue
            user.sendPacket(banchoPacket)
        }
    }

    fun addUser(banchoUser: BanchoUser) {
        connectedUsers.add(banchoUser)
        update()
    }

    fun removePlayer(banchoUser: BanchoUser) {
        connectedUsers.remove(banchoUser)
        update()
    }

    fun sendMessage(chatMessage: GlobalChatMessage) {
        sendPacketToAllExcluding(SendMessagePacket(chatMessage), chatMessage.source)
    }

    private fun update() {
        Hirasawa.sendBanchoPacketToAll(ChannelAvailablePacket(this))
    }

}