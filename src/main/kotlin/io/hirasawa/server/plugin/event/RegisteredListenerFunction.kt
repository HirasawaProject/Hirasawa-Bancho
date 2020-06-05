package io.hirasawa.server.plugin.event

import kotlin.reflect.KFunction

class RegisteredListenerFunction(private val eventListener: EventListener, private val function: KFunction<*>) {
    fun call(event: HirasawaEvent) {
        function.call(eventListener, event)
    }
}