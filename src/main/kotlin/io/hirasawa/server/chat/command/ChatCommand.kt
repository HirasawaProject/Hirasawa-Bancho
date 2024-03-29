package io.hirasawa.server.chat.command

abstract class ChatCommand(val name: String, val description: String, val permission: String = "") {
    abstract fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean
}