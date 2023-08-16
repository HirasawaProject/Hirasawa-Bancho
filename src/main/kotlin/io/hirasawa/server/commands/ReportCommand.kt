package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.database.tables.ReportsTable
import io.hirasawa.server.database.tables.UsersTable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ReportCommand: ChatCommand("report", "Report a player to the mods", "hirasawa.command.report") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (args.size < 2) {
            context.respond("!report <username> <reason>")
            return false
        }

        val reporteeUsername = args.first()
        val reason = args.slice(1 until args.size).joinToString(" ")

        lateinit var reporter: BanchoUser
        if (context.sender is BanchoUser) {
            reporter = context.sender
        } else {
            context.respond("Console cannot report")
            return false
        }

        val reportee = Hirasawa.databaseToObject<BanchoUser>(BanchoUser::class, transaction {
            UsersTable.select {
                UsersTable.username eq reporteeUsername
            }.first()
        })

        if (reportee == null) {
            context.respond("Cannot find user $reporteeUsername")
            return false
        }

        transaction {
            ReportsTable.insertAndGetId {
                it[ReportsTable.reporterId] = reporter.id
                it[ReportsTable.userId] = reportee.id
                it[ReportsTable.reason] = reason
            }
        }

        Hirasawa.chatEngine.handleChat(Hirasawa.banchoBot, "#lounge", "User ${reporter.username} reported " +
                "${reportee.username} for $reason")

        context.respond("Thank you! Your report has been noted and staff have been informed")

        return true
    }

}