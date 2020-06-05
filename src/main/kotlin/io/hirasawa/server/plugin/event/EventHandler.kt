package io.hirasawa.server.plugin.event

annotation class EventHandler(val eventPriority: EventPriority = EventPriority.NORMAL,
                              val bypassCancel: Boolean = false)