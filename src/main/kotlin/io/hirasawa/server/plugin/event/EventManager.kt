package io.hirasawa.server.plugin.event

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

class EventManager {
    val registeredEvents = HashMap<EventPriority, ArrayList<RegisteredListenerFunction>>()

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
                val registeredFunction = RegisteredListenerFunction(eventListener, function)
                this.registeredEvents[eventHandler.eventPriority]?.add(registeredFunction)
            }
        }
    }
}