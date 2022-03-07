package io.hirasawa.server.bancho.chat

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.message.GlobalChatMessage
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.ChannelAvailablePacket
import io.hirasawa.server.bancho.packets.SendMessagePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.irc.clientcommands.Privmsg
import io.hirasawa.server.irc.objects.IrcUser

data class ChatChannel(val name: String, val description: String, val autojoin: Boolean) {
    val connectedUsers = ArrayList<User>()
    val size get() = connectedUsers.size.toShort()

    fun sendPacketToAll(banchoPacket: BanchoPacket) {
        for (user in connectedUsers) {
            if (user is BanchoUser) {
                user.sendPacket(banchoPacket)
            }
        }
    }

    private fun sendPacketToAllExcluding(banchoPacket: BanchoPacket, exclude: User) {
        for (user in connectedUsers) {
            if (user == exclude) continue
            if (user is BanchoUser) {
                user.sendPacket(banchoPacket)
            }
        }
    }

    fun addUser(user: User) {
        connectedUsers.add(user)
        update()
    }

    fun removePlayer(user: User) {
        connectedUsers.remove(user)
        update()
    }

    fun sendMessage(chatMessage: GlobalChatMessage) {
        for (user in connectedUsers) {
            if (chatMessage.source == user) continue
            when (user) {
                is BanchoUser -> {
                    user.sendPacket(SendMessagePacket(chatMessage))
                }
                is IrcUser -> {
                    user.sendReply(Privmsg(chatMessage))
                }
            }
        }
    }

    private fun update() {
        Hirasawa.sendBanchoPacketToAll(ChannelAvailablePacket(this))
    }

}