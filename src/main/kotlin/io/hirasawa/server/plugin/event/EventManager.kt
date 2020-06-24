package io.hirasawa.server.plugin.event

import io.hirasawa.server.plugin.HirasawaPlugin
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions

class EventManager {
    val registeredEvents = HashMap<EventPriority, HashMap<String, ArrayList<RegisteredListenerFunction>>>()

    init {
        for (priority in EventPriority.values()) {
            registeredEvents[priority] = HashMap()
        }
    }

    fun callEvent(hirasawaEvent: HirasawaEvent) {
        for (priority in EventPriority.values()) {

            val listeners = registeredEvents[priority]?.get(hirasawaEvent::class.qualifiedName) ?: continue

            for (function in listeners) {
                if (hirasawaEvent is Cancelable) {
                    if (hirasawaEvent.isCancelled && !function.annotation.bypassCancel) continue
                }
                function.call(hirasawaEvent)
            }
        }
    }

    fun registerEvent(eventListener: EventListener, plugin: HirasawaPlugin) {
        val kClass = eventListener::class
        for (function in kClass.functions) {
            val eventHandler = function.findAnnotation<EventHandler>()
            if (eventHandler != null) {
                val registeredFunction = RegisteredListenerFunction(eventListener, function, plugin)
                if (registeredFunction.eventClass !in this.registeredEvents[eventHandler.eventPriority]!!.keys) {
                    this.registeredEvents[eventHandler.eventPriority]?.set(registeredFunction.eventClass, ArrayList())
                }

                this.registeredEvents[eventHandler.eventPriority]?.get(registeredFunction.eventClass)
                    ?.add(registeredFunction)
            }
        }
    }

    fun removeEvents(plugin: HirasawaPlugin) {
        for (priority in EventPriority.values()) {
            val events = registeredEvents[priority] ?: continue
            for (event in events.values) {
                val iterator = event.iterator()
                while (iterator.hasNext()) {
                    val listener = iterator.next()
                    if (listener.plugin == plugin) {
                        iterator.remove()
                    }
                }
            }
        }
    }
}