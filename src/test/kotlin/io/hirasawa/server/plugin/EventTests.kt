package io.hirasawa.server.plugin

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.plugin.event.EventHandler
import io.hirasawa.server.plugin.event.EventListener
import io.hirasawa.server.plugin.event.EventPriority
import io.hirasawa.server.plugin.event.HirasawaEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

class EventTests {
    @Test
    fun testCanCallAndListenToCustomEventsWithPriority() {
        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler(EventPriority.NORMAL)
            fun onTestEvent(testEvent: TestEvent) {
                testEvent.test = 1
            }
        })

        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler(EventPriority.LOW)
            fun onTestEvent(testEvent: TestEvent) {
                testEvent.test = 2
            }
        })

        val event = TestEvent(0)

        Hirasawa.eventHandler.callEvent(event)

        assertEquals(2, event.test)
    }



    class TestEvent(var test: Int): HirasawaEvent
}