package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.objects.Beatmap
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CreateMatchCommand: ChatCommand("mpcreate", "Says pong back") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        Hirasawa.multiplayer.createMatch("Test", Beatmap(
            transaction {
                BeatmapsTable.select {
                    BeatmapsTable.id eq 1
                }.first()
            }
        ), Hirasawa.hirasawaBot)

        return true
    }

}