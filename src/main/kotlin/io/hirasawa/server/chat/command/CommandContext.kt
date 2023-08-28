package io.hirasawa.server.chat.command

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.message.GlobalChatMessage

data class CommandContext(val sender: CommandSender, val channel: ChatChannel) {
    fun respond(message: String) {
        Hirasawa.chatEngine.handleChat(GlobalChatMessage(Hirasawa.banchoBot, channel, message))
    }
}