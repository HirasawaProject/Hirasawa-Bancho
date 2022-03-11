package io.hirasawa.server.commands

import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.bancho.user.BanchoUser

class TestCommand: ChatCommand("test", "Test command", "hirasawa.command.test") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (context.sender is BanchoUser) {
            context.respond("This is a test")
        } else {
            context.respond("baka console")
        }
        return true
    }

}