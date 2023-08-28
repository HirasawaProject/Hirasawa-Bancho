package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.chat.command.ChatCommand
import io.hirasawa.server.chat.command.CommandContext
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.chat.ChatChannelMetadata
import io.hirasawa.server.chat.GlobalChatChannel
import io.hirasawa.server.helpers.paginate

class PrivateChannelCommand: ChatCommand("privchan", "Creates a test channel") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        if (context.sender !is User) {
            context.respond("You must be a user to use this command")
            return false
        }
        Hirasawa.chatEngine.addUserToPrivateChannel(context.sender, channel)
        return true
    }

    companion object {
        val channel = GlobalChatChannel(ChatChannelMetadata(
            "#testchan",
            "Test channel",
            true
        ))
    }
}