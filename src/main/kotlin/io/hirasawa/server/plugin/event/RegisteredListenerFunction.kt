package io.hirasawa.server.plugin.event

import io.hirasawa.server.plugin.HirasawaPlugin
import kotlin.reflect.KFunction

class RegisteredListenerFunction(private val eventListener: EventListener, private val function: KFunction<*>,
                                 val plugin: HirasawaPlugin) {
    val eventClass = function.parameters[1].type.toString()
    val annotation = function.annotations[0] as EventHandler

    fun call(event: HirasawaEvent) {
        function.call(eventListener, event)
    }
}