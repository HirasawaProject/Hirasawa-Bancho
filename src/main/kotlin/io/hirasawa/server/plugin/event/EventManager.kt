package io.hirasawa.server.plugin.event

import io.hirasawa.server.plugin.event.bancho.BanchoUserLoginEvent
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.javaMethod

class EventManager {
    val registeredEvents = HashMap<EventPriority, ArrayList<KFunction<*>>>()

    init {
        for (priority in EventPriority.values()) {
            registeredEvents[priority] = ArrayList()
        }
    }

    fun callEvent(hirasawaEvent: HirasawaEvent) {
        for (priority in EventPriority.values()) {
            if (priority in registeredEvents.keys) {
                for (function in registeredEvents[priority]!!) {
                    function.call(hirasawaEvent)
                }
            }
        }
    }

    fun registerEvent(eventListener: EventListener) {
        val kClass = eventListener::class
        for (function in kClass.functions) {
            val eventHandler = function.findAnnotation<EventHandler>()
            if (eventHandler != null) {
                this.registeredEvents[eventHandler.eventPriority]?.add(function)
            }
        }
    }
}