package io.hirasawa.server.bancho.enums

enum class MatchType {
    STANDARD,
    POWERPLAY;

    companion object {
        fun fromId(id: Byte): MatchType = MatchType.values()[id.toInt()]
    }
}