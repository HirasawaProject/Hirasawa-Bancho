package io.hirasawa.server.enums

import io.hirasawa.server.objects.Mods

enum class Mod(val id: Int) {
    NO_MOD(0),
    NO_FAIL(1 shl 0),
    EASY(1 shl 1),
    TOUCH_DEVICE(1 shl 2), // Not selected by user, added when touch device is detected during play
    HIDDEN(1 shl 3),
    HARD_ROCK(1 shl 4),
    SUDDEN_DEATH(1 shl 5),
    DOUBLE_TIME(1 shl 6),
    RELAX(1 shl 7),
    HALF_TIME(1 shl 8),
    NIGHTCORE(1 shl 9), // When nightcore is active doubletime will be too
    FLASHLIGHT(1 shl 10),
    AUTOPLAY(1 shl 11),
    SPUN_OUT(1 shl 12),
    AUTOPILOT(1 shl 13),
    PERFECT(1 shl 14),
    KEY4(1 shl 15),
    KEY5(1 shl 16),
    KEY6(1 shl 17),
    KEY7(1 shl 18),
    KEY8(1 shl 19),
    FADE_IN(1 shl 20),
    RANDOM(1 shl 21),
    CINEMA(1 shl 22),
    TARGET(1 shl 23),
    KEY9(1 shl 24),
    KEY_COOP(1 shl 25),
    KEY1(1 shl 26),
    KEY3(1 shl 27),
    KEY2(1 shl 28),
    SCORE_V2(1 shl 29),
    KEY_MODS((KEY1 + KEY2 + KEY3 + KEY4 + KEY5 + KEY6 + KEY7 + KEY8).toInt());

    operator fun plus(mod: Mod): Mods {
        return Mods(arrayListOf(this, mod))
    }
}