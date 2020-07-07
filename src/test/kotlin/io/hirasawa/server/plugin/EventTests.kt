package io.hirasawa.server.plugin

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.plugin.event.*
import io.hirasawa.server.plugin.event.plugin.PluginLoadEvent
import io.hirasawa.server.plugin.event.plugin.PluginUnloadEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventTests {
    private val plugin = object: HirasawaPlugin() {
        override fun onEnable() {}

        override fun onDisable() {}
    }

    @Test
    fun testCanCallAndListenToCustomEventsWithPriority() {
        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler(EventPriority.NORMAL)
            fun onTestEvent(testEvent: TestEvent) {
                testEvent.test = 1
            }
        }, this.plugin)

        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler(EventPriority.LOW)
            fun onTestEvent(testEvent: TestEvent) {
                testEvent.test = 2
            }
        }, this.plugin)

        val event = TestEvent(0)

        Hirasawa.eventHandler.callEvent(event)

        assertEquals(2, event.test)
    }

    @Test
    fun testCanBypassEventCancellation() {
        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler(EventPriority.HIGHEST)
            fun onTestEvent(testEvent: TestCanceledEvent) {
                testEvent.isCancelled = true
            }
        }, this.plugin)

        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler(EventPriority.NORMAL, true)
            fun onTestEvent(testEvent: TestCanceledEvent) {
                testEvent.test = 1
            }
        }, this.plugin)

        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler(EventPriority.LOW)
            fun onTestEvent(testEvent: TestCanceledEvent) {
                testEvent.test = 2
            }
        }, this.plugin)

        val event = TestCanceledEvent(0)

        Hirasawa.eventHandler.callEvent(event)

        assertEquals(1, event.test)
    }

    @Test
    fun testCanEventListenerBeRemoved() {
        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler(EventPriority.HIGHEST)
            fun onTestEvent(testEvent: SecondaryTestEvent) {
                testEvent.test = 1
            }
        }, this.plugin)

        Hirasawa.eventHandler.removeEvents(plugin)

        val event = SecondaryTestEvent(0)

        Hirasawa.eventHandler.callEvent(event)

        assertEquals(0, event.test)
    }

    @Test
    fun testPluginLoadUnloadEvents() {
        val testPlugin = object: HirasawaPlugin() {
            override fun onEnable() {}
            override fun onDisable() {}
        }

        var loaded = false
        var unloaded = false

        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler
            @Suppress("UNUSED_PARAMETER")
            fun onTestEvent(pluginLoadEvent: PluginLoadEvent) {
                loaded = true
            }
        }, this.plugin)

        Hirasawa.eventHandler.registerEvent(object: EventListener {
            @EventHandler
            @Suppress("UNUSED_PARAMETER")
            fun onTestEvent(pluginUnloadEvent: PluginUnloadEvent) {
                unloaded = true
            }
        }, this.plugin)

        Hirasawa.pluginManager.loadPlugin(testPlugin, PluginDescriptor("name", "version", "author", "main"))
        Hirasawa.pluginManager.unloadPlugin("name")

        assertTrue(loaded)
        assertTrue(unloaded)
    }



    class TestEvent(var test: Int): HirasawaEvent
    class TestCanceledEvent(var test: Int): HirasawaEvent, Cancelable()
    class SecondaryTestEvent(var test: Int): HirasawaEvent
}