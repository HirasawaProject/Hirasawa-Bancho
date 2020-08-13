package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.command.ChatCommand
import io.hirasawa.server.bancho.chat.command.CommandContext
import io.hirasawa.server.bancho.chat.command.CommandSender
import io.hirasawa.server.bancho.chat.message.PrivateChatMessage
import io.hirasawa.server.bancho.packets.SendMessagePacket
import io.hirasawa.server.bancho.user.BanchoUser

class UserInfoCommand: ChatCommand("userinfo", "Command to give internal information about a user") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (args.isEmpty()) {
            context.respond("!userinfo <username>")
            return false
        }

        val user = Hirasawa.banchoUsers[args[0]]
        if (user == null) {
            context.respond("User doesn't exist")
            return false
        }

        context.respond("Username: ${user.username}")
        context.respond("Client permissions: ${user.clientPermissions}")
        context.respond("Last keep alive: ${user.lastKeepAlive}")
        context.respond("Spectating: ${user.spectating?.username ?: "nobody"}")
        context.respond("Spectators:")
        for (spectator in user.spectators) {
            context.respond("* ${user.username}")
        }

        return true
    }

}