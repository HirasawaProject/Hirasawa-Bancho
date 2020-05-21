package io.hirasawa.server.bancho.chat.command

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.ChatChannel

data class CommandContext(val sender: CommandSender, val channel: ChatChannel) {
    fun respond(message: String) {
        Hirasawa.chatEngine.handleChat(Hirasawa.hirasawaBot, channel.name, message)
    }
}