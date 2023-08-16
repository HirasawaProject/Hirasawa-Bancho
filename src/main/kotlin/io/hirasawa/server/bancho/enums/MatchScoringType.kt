package io.hirasawa.server.bancho.enums

enum class MatchScoringType {
    SCORE,
    ACCURACY,
    COMBO,
    SCORE_V2;

    companion object {
        fun fromId(id: Byte): MatchScoringType = MatchScoringType.values()[id.toInt()]
    }
}