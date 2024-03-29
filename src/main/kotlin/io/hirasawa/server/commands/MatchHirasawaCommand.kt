package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext

class MatchHirasawaCommand: ChatCommand("mpbot", "Says pong back") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (context.sender is BanchoUser) {
            context.sender.currentMatch?.addToMatch(Hirasawa.banchoBot)
        }

        return true
    }

}