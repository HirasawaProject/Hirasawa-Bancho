package io.hirasawa.server.bancho.enums

enum class MatchSpecialMode {
    NONE,
    FREE_MOD;

    infix fun has(other: MatchSpecialMode): Boolean {
        return (this.ordinal and other.ordinal > 0)
    }

    companion object {
        fun fromId(id: Byte): MatchSpecialMode = MatchSpecialMode.values()[id.toInt()]
    }
}