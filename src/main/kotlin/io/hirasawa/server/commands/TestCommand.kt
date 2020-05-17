package io.hirasawa.server.commands

import io.hirasawa.server.bancho.chat.command.ChatCommand
import io.hirasawa.server.bancho.chat.command.CommandSender
import io.hirasawa.server.bancho.chat.message.PrivateChatMessage
import io.hirasawa.server.bancho.packets.SendMessagePacket
import io.hirasawa.server.bancho.user.BanchoUser

class TestCommand: ChatCommand("test") {
    override fun onCommand(sender: CommandSender, command: String, args: List<String>): Boolean {
        if (sender is BanchoUser) {
            sender.sendPacket(SendMessagePacket(PrivateChatMessage(sender, sender, "This is a test")))
        } else {
            println("baka console")
        }
        return true
    }

}