package io.hirasawa.server.chat

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.message.GlobalChatMessage
import io.hirasawa.server.bancho.packets.BanchoPacket
import io.hirasawa.server.bancho.packets.ChannelAvailablePacket
import io.hirasawa.server.bancho.packets.SendMessagePacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.chat.enums.ChatChannelVisibility
import io.hirasawa.server.irc.clientcommands.*
import io.hirasawa.server.irc.objects.IrcUser
import io.hirasawa.server.objects.UserMap

abstract class ChatChannel(val metadata: ChatChannelMetadata,
                           connectedUsers: UserMap<out User> = UserMap(),
                           val visibility: ChatChannelVisibility = ChatChannelVisibility.PRIVATE) {
    val connectedUsers: UserMap<User> = UserMap()
    private val addBindId: Int
    private val removeBindId: Int
    init {
        connectedUsers.values.forEach {
            this.connectedUsers.add(it)
        }
        addBindId = connectedUsers.bind(UserMap.BindType.ADD) {
            addUser(it ?: return@bind)
        }
        removeBindId = connectedUsers.bind(UserMap.BindType.REMOVE) {
            addUser(it ?: return@bind)
        }
        connectedUsers.bind(UserMap.BindType.CLOSE) {
            this.close()
        }
    }
    val size get() = connectedUsers.size.toShort()

    /**
     * Send BanchoPacket to all BanchoUsers connected to this ChatChannel
     *
     * @param banchoPacket The packet to send
     */
    fun sendBanchoPacketToAll(banchoPacket: BanchoPacket) {
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
        if (visibility == ChatChannelVisibility.PRIVATE) {
            Hirasawa.chatEngine.addUserToPrivateChannel(user, this)
        }
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
            if (visibility == ChatChannelVisibility.PRIVATE) {
                user.revokeChatChannel(this)
            }
            update()


            if (visibility == ChatChannelVisibility.PRIVATE) {
                Hirasawa.chatEngine.removeUserFromPrivateChannel(user, this)
            }
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

    fun close() {
        connectedUsers.unbind(UserMap.BindType.ADD, addBindId)
        connectedUsers.unbind(UserMap.BindType.REMOVE, removeBindId)
        Hirasawa.chatEngine.removeChannel(this)
    }

    private fun update() {
        when (visibility) {
            ChatChannelVisibility.PRIVATE -> {
                sendBanchoPacketToAll(ChannelAvailablePacket(this))
            }
            ChatChannelVisibility.PUBLIC -> {
                Hirasawa.sendBanchoPacketToAll(ChannelAvailablePacket(this))
            }
        }
    }

}