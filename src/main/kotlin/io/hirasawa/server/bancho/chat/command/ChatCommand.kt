package io.hirasawa.server.bancho.chat.command

abstract class ChatCommand(val name: String) {
    abstract fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean
}