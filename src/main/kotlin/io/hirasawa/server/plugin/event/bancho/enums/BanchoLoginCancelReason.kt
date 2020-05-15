package io.hirasawa.server.plugin.event.bancho.enums

enum class BanchoLoginCancelReason(val id: Int) {
    NOT_CANCELLED(0),
    AUTHENTICATION_FAILED(-1),
    OLD_VERSION(-2),
    BANNED(-3),
    ERROR_OCCURRED(-5),
    REQUIRES_SUPPORTER(-6),
    REQUIRES_PASSWORD_RESET(-7),
    REQUIRES_VERIFICATION(-8);

}