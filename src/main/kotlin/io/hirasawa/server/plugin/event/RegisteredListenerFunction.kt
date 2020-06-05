package io.hirasawa.server.plugin.event

import kotlin.reflect.KFunction

class RegisteredListenerFunction(private val eventListener: EventListener, private val function: KFunction<*>) {
    val eventClass: String = function.parameters[1].type.toString()

    fun call(event: HirasawaEvent) {
        function.call(eventListener, event)
    }
}