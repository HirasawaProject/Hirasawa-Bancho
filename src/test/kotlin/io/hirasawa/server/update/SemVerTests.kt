package io.hirasawa.server.update

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SemVerTests {
    @Test
    fun testCanParseVersions() {
        val ver1 = SemVer.parse("0.0.4")
        assertEquals(0, ver1.major)
        assertEquals(0, ver1.minor)
        assertEquals(4, ver1.patch)
        assertEquals("", ver1.preRelease)
        assertEquals("", ver1.buildMetadata)
        assertFalse(ver1.isPreRelease)
        assertFalse(ver1.hasBuildMetadata)

        val ver2 = SemVer.parse("1.2.3")
        assertEquals(1, ver2.major)
        assertEquals(2, ver2.minor)
        assertEquals(3, ver2.patch)
        assertEquals("", ver2.preRelease)
        assertEquals("", ver2.buildMetadata)
        assertFalse(ver2.isPreRelease)
        assertFalse(ver2.hasBuildMetadata)

        val ver3 = SemVer.parse("10.20.30")
        assertEquals(10, ver3.major)
        assertEquals(20, ver3.minor)
        assertEquals(30, ver3.patch)
        assertEquals("", ver3.preRelease)
        assertEquals("", ver3.buildMetadata)
        assertFalse(ver3.isPreRelease)
        assertFalse(ver3.hasBuildMetadata)

        val ver4 = SemVer.parse("1.1.2-prerelease+meta")
        assertEquals(1, ver4.major)
        assertEquals(1, ver4.minor)
        assertEquals(2, ver4.patch)
        assertEquals("prerelease", ver4.preRelease)
        assertEquals("meta", ver4.buildMetadata)
        assertTrue(ver4.isPreRelease)
        assertTrue(ver4.hasBuildMetadata)

        val ver5 = SemVer.parse("1.1.2+meta")
        assertEquals(1, ver5.major)
        assertEquals(1, ver5.minor)
        assertEquals(2, ver5.patch)
        assertEquals("", ver5.preRelease)
        assertEquals("meta", ver5.buildMetadata)
        assertFalse(ver5.isPreRelease)
        assertTrue(ver5.hasBuildMetadata)

        val ver6 = SemVer.parse("1.1.2-alpha")
        assertEquals(1, ver6.major)
        assertEquals(1, ver6.minor)
        assertEquals(2, ver6.patch)
        assertEquals("alpha", ver6.preRelease)
        assertEquals("", ver6.buildMetadata)
        assertTrue(ver6.isPreRelease)
        assertFalse(ver6.hasBuildMetadata)
    }

    @Test
    fun testWillInvalidSemVerBeRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("1")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("1.2")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("1.2.3-0123")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("1.2.3-0123.0123")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("1.2,3+.123")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("+invalid")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("-invalid")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("-invalid+invalid")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("-invalid.01")
        }
        assertThrows(IllegalArgumentException::class.java) {
            SemVer.parse("invalid")
        }
    }

    @Test
    fun testSemVerComparisons() {
        assertTrue(SemVer.parse("2.0.0") > SemVer.parse("1.0.0"))
        assertTrue(SemVer.parse("2.1.0") > SemVer.parse("2.0.0"))
        assertTrue(SemVer.parse("2.1.1") > SemVer.parse("2.1.0"))

        assertTrue(SemVer.parse("1.0.0") == SemVer.parse("1.0.0"))

        assertFalse(SemVer.parse("2.0.0") < SemVer.parse("1.0.0"))
        assertFalse(SemVer.parse("2.1.0") < SemVer.parse("2.0.0"))
        assertFalse(SemVer.parse("2.1.1") < SemVer.parse("2.1.0"))
    }
}