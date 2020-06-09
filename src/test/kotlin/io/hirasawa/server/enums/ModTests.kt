package io.hirasawa.server.enums

import org.junit.jupiter.api.Test
import io.hirasawa.server.polyfill.plus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class ModTests {
    @Test
    fun testModIdsToModArray() {
        val modIds = Mod.PERFECT + Mod.DOUBLE_TIME + Mod.HARD_ROCK

        val mods = Mod.idToModArray(modIds)
        assertTrue(mods.contains(Mod.PERFECT))
        assertTrue(mods.contains(Mod.DOUBLE_TIME))
        assertTrue(mods.contains(Mod.HARD_ROCK))
        assertEquals(mods.size, 3)
    }
}