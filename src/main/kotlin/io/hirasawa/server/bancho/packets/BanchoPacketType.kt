package io.hirasawa.server.bancho.packets


enum class BanchoPacketType(val id: Short) {
    UNKNOWN(-1),
    OSU_SEND_USER_STATS(0),
    OSU_SEND_IRC_MESSAGE(1),
    OSU_EXIT(2),
    OSU_REQUEST_STATUS_UPDATE(3),
    OSU_PONG(4),
    BANCHO_LOGIN_REPLY(5),

    BANCHO_HANDLE_OSU_UPDATE(11),

    BANCHO_CHANNEL_JOIN_SUCCESS(64),
    BANCHO_CHANNEL_AVAILABLE(65),

    BANCHO_CHANNEL_AVAILABLE_AUTOJOIN(67),

    BANCHO_PROTOCOL_NEGOTIATION(75),

    BANCHO_USER_PRESENCE(83),

    BANCHO_CHANNEL_LISTING_COMPLETE(89),

    BANCHO_RESTART(86);

    companion object {
        private val map = BanchoPacketType.values().associateBy(BanchoPacketType::id)
        fun fromId(type: Short): BanchoPacketType = map[type] ?: UNKNOWN
    }
}