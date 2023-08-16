package io.hirasawa.server.objects

import io.hirasawa.server.bancho.enums.MatchSpecialMode
import io.hirasawa.server.enums.Mod

class Mods(private val modsList: ArrayList<Mod> = ArrayList()) {
    val size: Int
        get() = modsList.size

    fun toArrayList(): ArrayList<Mod> {
        return modsList
    }

    fun toInt(): Int {
        return modsList.sumOf { it.id }
    }

    operator fun plus(mod: Mod): Mods {
        if (!modsList.contains(mod)) {
            modsList.add(mod)
        }
        return this
    }

    operator fun plus(mods: Mods): Mods {
        for (mod in mods.toArrayList()) {
            if (!modsList.contains(mod)) {
                modsList.add(mod)
            }
        }
        return this
    }

    operator fun minus(mod: Mod): Mods {
        modsList.remove(mod)
        return this
    }

    operator fun minus(mods: Mods): Mods {
        return fromInt(this.toInt() - mods.toInt())
    }

    operator fun contains(mod: Mod): Boolean {
        return modsList.contains(mod)
    }

    infix fun and(mod: Mod): Mods {
        return fromInt(this.toInt() and mod.id)
    }

    infix fun and(mods: Mods): Mods {
        return fromInt(this.toInt() and mods.toInt())
    }

    operator fun iterator(): Iterator<Mod> {
        return modsList.iterator()
    }

    companion object {
        fun fromInt(id: Int): Mods {
            val modsList = ArrayList<Mod>()
            for (mod in Mod.values()) {
                if ((id and mod.id) > 0) {
                    modsList.add(mod)
                }
            }

            return Mods(modsList)
        }
    }
}