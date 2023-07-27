package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.objects.Beatmap
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class MatchHirasawaCommand: ChatCommand("mpbot", "Says pong back") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (context.sender is BanchoUser) {
            context.sender.currentMatch?.addToMatch(Hirasawa.hirasawaBot)
        }

        return true
    }

}