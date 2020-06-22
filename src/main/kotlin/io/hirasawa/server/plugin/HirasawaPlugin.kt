package io.hirasawa.server.plugin

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.command.ChatCommand
import io.hirasawa.server.logger.PluginLogger
import io.hirasawa.server.plugin.event.EventListener

abstract class HirasawaPlugin: IHirasawaPlugin {
    lateinit var pluginDescriptor: PluginDescriptor
    lateinit var logger: PluginLogger

    fun registerEvent(eventListener: EventListener) {
        Hirasawa.eventHandler.registerEvent(eventListener)
    }

    fun registerCommand(chatCommand: ChatCommand) {
        Hirasawa.chatEngine.chatCommands[chatCommand.name] = chatCommand
    }
}