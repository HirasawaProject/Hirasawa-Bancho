package io.hirasawa.server.bancho.enums

enum class MatchSlotTeam {
    NONE,
    BLUE,
    RED;

    fun toggle(): MatchSlotTeam {
        return when(this) {
            NONE -> NONE
            BLUE -> RED
            RED -> BLUE
        }
    }

    companion object {
        fun fromId(id: Byte): MatchSlotTeam = MatchSlotTeam.values()[id.toInt()]
    }
}