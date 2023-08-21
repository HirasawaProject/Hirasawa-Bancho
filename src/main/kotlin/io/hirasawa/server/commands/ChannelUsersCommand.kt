package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.chat.ChatChannelMetadata
import io.hirasawa.server.chat.GlobalChatChannel
import io.hirasawa.server.helpers.paginate
import io.hirasawa.server.irc.objects.IrcUser

class ChannelUsersCommand: ChatCommand("chanusers", "List connected users in a channel") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        context.respond("Connected users (${context.channel.connectedUsers.size}):")
        for (user in context.channel.connectedUsers) {
            val client = if (user is BanchoUser) {
                "Bancho"
            } else if (user is IrcUser) {
                "IRC"
            } else {
                "Unknown"
            }
            context.respond("* ${user.username} ($client)")
        }
        return true
    }
}