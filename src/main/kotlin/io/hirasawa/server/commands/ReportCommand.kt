package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.command.ChatCommand
import io.hirasawa.server.bancho.chat.command.CommandContext
import io.hirasawa.server.bancho.chat.command.ConsoleCommandSender
import io.hirasawa.server.bancho.user.BanchoUser

class ReportCommand: ChatCommand("report", "Report a player to the mods", "hirasawa.command.report") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (args.size < 2) {
            context.respond("!report <username> <reason>")
            return false
        }

        val username = args.first()
        val reason = args.slice(1..Int.MAX_VALUE)

        var reportee = "UNKNOWN"
        if (context.sender is BanchoUser) {
            reportee = context.sender.username
        } else if (context.sender is ConsoleCommandSender) {
            reportee = "CONSOLE"
        }

        Hirasawa.chatEngine.handleChat(Hirasawa.hirasawaBot, "#lounge", "User $reportee reported $username for $reason")

        return true
    }

}