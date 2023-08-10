package io.hirasawa.server.bancho.enums

enum class Mode {
    OSU,
    TAIKO,
    CATCH_THE_BEAT,
    MANIA;

    companion object {
        fun fromId(id: Byte): Mode = Mode.values()[id.toInt()]
    }
}