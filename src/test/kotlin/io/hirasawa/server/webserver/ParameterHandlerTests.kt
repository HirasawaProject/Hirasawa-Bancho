package io.hirasawa.server.webserver

import io.hirasawa.server.webserver.handlers.ParameterHandler
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ParameterHandlerTests {
    @Test
    fun testParameterHandlerProcessesEmptyCorrectly() {
        val parameters = ParameterHandler("".toByteArray()).parameters

        assertTrue(parameters.isEmpty())
    }
}