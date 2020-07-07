package io.hirasawa.server.helpers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HelperTests {
    @Test
    fun testListPaginate() {
        val list = listOf("This", "is", "a", "test", "for", "the", "pagination", "helper").paginate(3)

        println(list)

        assertEquals(3, list.size)
        assertEquals(3, list.first().size)
        assertEquals(2, list.last().size)
    }
}