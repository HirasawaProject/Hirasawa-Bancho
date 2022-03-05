package io.hirasawa.server.objects

import io.hirasawa.server.enums.Mod
import org.junit.jupiter.api.Test
import io.hirasawa.server.polyfill.plus
import org.junit.jupiter.api.Assertions.*

class ModsTests {
    @Test
    fun testModAddition() {
        val mods = Mod.PERFECT + Mod.DOUBLE_TIME + Mod.HARD_ROCK

        assertTrue(mods.contains(Mod.PERFECT))
        assertTrue(mods.contains(Mod.DOUBLE_TIME))
        assertTrue(mods.contains(Mod.HARD_ROCK))
        assertEquals(mods.size, 3)
    }

    @Test
    fun testModMinus() {
        var mods = Mod.PERFECT + Mod.DOUBLE_TIME + Mod.HARD_ROCK
        mods -= Mod.DOUBLE_TIME

        assertTrue(mods.contains(Mod.PERFECT))
        assertFalse(mods.contains(Mod.DOUBLE_TIME))
        assertTrue(mods.contains(Mod.HARD_ROCK))
        assertEquals(mods.size, 2)
    }

    @Test
    fun testModsToInt() {
        val mods = Mod.PERFECT + Mod.DOUBLE_TIME + Mod.HARD_ROCK

        assertEquals(16464, mods.toInt())
    }

    @Test
    fun testModsFromInt() {
        val mods = Mods.fromInt(16464)

        assertTrue(mods.contains(Mod.PERFECT))
        assertTrue(mods.contains(Mod.DOUBLE_TIME))
        assertTrue(mods.contains(Mod.HARD_ROCK))
        assertEquals(mods.size, 3)
    }

    @Test
    fun testModsToIntThenBackFromInt() {
        val mods = Mod.DOUBLE_TIME + Mod.NIGHTCORE + Mod.FLASHLIGHT

        val mods2 = Mods.fromInt(mods.toInt())
        assertTrue(mods2.contains(Mod.DOUBLE_TIME))
        assertTrue(mods2.contains(Mod.NIGHTCORE))
        assertTrue(mods2.contains(Mod.FLASHLIGHT))
        assertEquals(mods2.size, 3)
    }
}