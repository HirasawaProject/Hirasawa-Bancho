package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.database.tables.UsersTable
import io.hirasawa.server.database.tables.UsersTable.username
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class SetupIrcCommand: ChatCommand("setupirc", "Sets up IRC access for your account") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (context.sender is User) {
            context.sender.sendPrivateMessage(Hirasawa.hirasawaBot, "Generating new IRC token")
            val messageDigest = MessageDigest.getInstance("MD5")
            val bytes = ByteArray(8)
            Random().nextBytes(bytes)
            val md5Bytes = messageDigest.digest(bytes)
            val number = BigInteger(1, md5Bytes)
            val token = number.toString(16).lowercase().substring(IntRange(0, 7))

            transaction {
                UsersTable.update( { username eq context.sender.username} ) {
                    it[ircToken] = token
                }
            }

            context.sender.sendPrivateMessage(Hirasawa.hirasawaBot, "Your IRC token is $token")
            context.sender.sendPrivateMessage(Hirasawa.hirasawaBot, "Use this as your password when connecting on IRC")
            context.sender.sendPrivateMessage(Hirasawa.hirasawaBot, "Run !setupirc again to generate a new token")
        }

        return true
    }

}