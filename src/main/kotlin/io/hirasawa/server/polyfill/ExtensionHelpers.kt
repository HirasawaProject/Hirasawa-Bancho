package io.hirasawa.server.polyfill

import io.hirasawa.server.enums.Mod

operator fun Int.plus(mod: Mod): Int {
    return this + mod.id
}