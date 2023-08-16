package io.hirasawa.server.commands

import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext

class PingCommand: ChatCommand("ping", "Says pong back") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        context.respond("Pong!")

        return true
    }

}