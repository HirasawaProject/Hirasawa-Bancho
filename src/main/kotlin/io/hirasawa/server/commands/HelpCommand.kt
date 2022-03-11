package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.helpers.paginate

class HelpCommand: ChatCommand("help", "Lists the commands you are able to do") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        val commands = Hirasawa.chatEngine.chatCommands.values.toList().paginate(10)

        var page = 1

        if (args.isNotEmpty()) {
            page = args[0].toInt()
        }

        context.respond("Page $page of ${commands.size}")

        if (page > commands.size || page < 1) {
            context.respond("Page doesn't exist!")
            return false
        }

        for (chatCommand in commands[page - 1]) {
            if (context.sender is User) {
                if (context.sender.hasPermission(chatCommand.first.permission)) {
                    context.respond(getCommandString(chatCommand.first))
                }
            } else {
                context.respond(getCommandString(chatCommand.first))
            }
        }

        return false
    }

    private fun getCommandString(command: ChatCommand): String {
        return "${command.name} - ${command.description}"
    }
}