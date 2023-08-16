package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.objects.Beatmap
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class MatchAllReadyCommand: ChatCommand("mpready", "Says pong back") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (context.sender is BanchoUser) {
            for(slotUser: BanchoUser? in context.sender.currentMatch?.slotUser!!) {
                context.sender.currentMatch!!.setReady(slotUser ?: continue, true)
            }
        }

        return true
    }

}