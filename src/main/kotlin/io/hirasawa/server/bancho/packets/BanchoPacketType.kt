package io.hirasawa.server.bancho.packets


enum class BanchoPacketType(val id: Short) {
    OSU_SEND_USER_STATS(0),
    OSU_SEND_IRC_MESSAGE(1),
    OSU_EXIT(2),
    OSU_REQUEST_STATUS_UPDATE(3),
    OSU_PONG(4),
    BANCHO_LOGIN_REPLY(5),

    BANCHO_CHANNEL_AVAILABLE(64)
}