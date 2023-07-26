package io.hirasawa.server.bancho.enums

enum class GameMode {
    OSU,
    TAIKO,
    CATCH_THE_BEAT,
    MANIA;

    companion object {
        fun fromId(id: Byte): GameMode = GameMode.values()[id.toInt()]
    }
}