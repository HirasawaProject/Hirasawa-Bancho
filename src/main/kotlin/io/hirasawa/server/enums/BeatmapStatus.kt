package io.hirasawa.server.enums

import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.objects.Beatmap

enum class BeatmapStatus(val id: Int) {
    NOT_SUBMITTED(-1),
    PENDING(0),
    UNKNOWN(1),
    RANKED(2),
    APPROVED(3);

    companion object {
        private val map = values().associateBy(BeatmapStatus::id)
        fun fromId(type: Int) = map[type] ?: UNKNOWN
    }
}