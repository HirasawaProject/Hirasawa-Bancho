package io.hirasawa.server.bancho.enums

enum class MatchTeamType {
    HEAD_TO_HEAD,
    TAG_COOP,
    TEAM_VS,
    TAG_TEAM_VS;

    companion object {
        fun fromId(id: Byte): MatchTeamType = MatchTeamType.values()[id.toInt()]
    }
}