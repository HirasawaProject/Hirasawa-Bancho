package io.hirasawa.server.chat.channel

import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.ChatChannelMetadata
import io.hirasawa.server.chat.message.GlobalChatMessage

/**
 * Internal channel for console messages
 */
class ConsoleChannel: ChatChannel(ChatChannelMetadata("!CONSOLE", "", false)) {
    override fun sendMessage(chatMessage: GlobalChatMessage) {
        println(chatMessage.message)
    }
}