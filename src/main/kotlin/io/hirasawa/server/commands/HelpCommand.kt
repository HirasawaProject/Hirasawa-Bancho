package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.command.ChatCommand
import io.hirasawa.server.bancho.chat.command.CommandContext
import io.hirasawa.server.bancho.user.User

class HelpCommand: ChatCommand("help", "Lists the commands you are able to do") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        val commands = Hirasawa.chatEngine.chatCommands

        if (context.sender is User) {
            for (chatCommand in commands.values) {
                if (context.sender.hasPermission(chatCommand.permission)) {
                    context.respond(getCommandString(chatCommand))
                }
            }
        } else {
            for (chatCommand in commands.values) {
                context.respond(getCommandString(chatCommand))
            }
        }


        return false
    }

    private fun getCommandString(command: ChatCommand): String {
        return "${command.name} - ${command.description}"
    }
}