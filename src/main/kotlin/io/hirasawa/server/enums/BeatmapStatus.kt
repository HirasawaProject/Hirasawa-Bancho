package io.hirasawa.server.enums

enum class BeatmapStatus(val id: Int) {
    NOT_SUBMITTED(-1),
    PENDING(0),
    UNKNOWN(1),
    RANKED(2),
    APPROVED(3);
}