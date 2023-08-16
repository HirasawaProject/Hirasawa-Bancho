package io.hirasawa.server.bancho.enums

import kotlin.experimental.and

enum class MatchSlotStatus(val id: Byte) {
    OPEN(1),
    LOCKED(2),
    NOT_READY(4),
    READY(8),
    NO_MAP(16),
    PLAYING(32),
    OCCUPIED(124),
    COMPLETE(64);

    infix fun has(id: Byte): Boolean {
        return (this.id and id) > 0
    }

    infix fun has(slotStatus: MatchSlotStatus): Boolean {
        return (this.id and slotStatus.id) > 0
    }

    companion object {
        fun fromId(id: Byte): MatchSlotStatus? {
            for (slotStatus: MatchSlotStatus in values()) {
                if (slotStatus.id == id) {
                    return slotStatus
                }
            }
            return null
        }
    }
}