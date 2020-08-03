package io.hirasawa.server.plugin

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.command.ChatCommand
import io.hirasawa.server.logger.PluginLogger
import io.hirasawa.server.plugin.event.EventListener

abstract class HirasawaPlugin: IHirasawaPlugin {
    lateinit var pluginDescriptor: PluginDescriptor
    lateinit var logger: PluginLogger

    /**
     * Register an event class to the event handler
     *
     * @param eventListener The event listener instance to be registered
     */
    fun registerEvents(eventListener: EventListener) {
        Hirasawa.eventHandler.registerEvents(eventListener, this)
    }

    /**
     * Register a command to the chat engine
     *
     * @param chatCommand The command instance to be registered
     */
    fun registerCommand(chatCommand: ChatCommand) {
        Hirasawa.chatEngine.chatCommands[chatCommand.name] = Pair(chatCommand, this)
    }
}