package io.hirasawa.server.chat

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.message.GlobalChatMessage
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.ChannelAvailablePacket
import io.hirasawa.server.bancho.packets.SendMessagePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.irc.clientcommands.*
import io.hirasawa.server.irc.objects.IrcUser

data class ChatChannel(val name: String, val description: String, val autojoin: Boolean) {
    val connectedUsers = ArrayList<User>()
    val size get() = connectedUsers.size.toShort()

    /**
     * Send BanchoPacket to all BanchoUsers connected to this ChatChannel
     *
     * @param banchoPacket The packet to send
     */
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

    private fun sendIrcReplyToAll(ircProtocolReply: IrcProtocolReply) {
        for (user in connectedUsers) {
            if (user is IrcUser) {
                user.sendReply(ircProtocolReply)
            }
        }
    }

    /**
     * Add user to ChatChannel, this will register them for all messages and inform other connected users that they
     * joined
     *
     * @param user The user to add
     */
    fun addUser(user: User) {
        connectedUsers.add(user)
        sendIrcReplyToAll(Join(this, user))
        update()
    }

    /**
     * Removes a user from this ChatChannel, this will unregister them for all messages and inform other connected users
     * that they have left
     *
     * @param user The user to remove
     */
    fun removeUser(user: User) {
        if (user in connectedUsers) {
            sendIrcReplyToAll(Part(this, user))
            connectedUsers.remove(user)
            update()
        }
    }

    /**
     * Queues a message to all connected clients in the ChatChannel other than the message source
     *
     * @param chatMessage The chat message to queue
     */
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