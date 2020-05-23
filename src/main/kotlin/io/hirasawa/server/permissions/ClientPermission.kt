package io.hirasawa.server.permissions

enum class ClientPermission(val byte: Byte) {
    NONE(0),
    NORMAL(1),
    BAT(2),
    SUPPORTER(4),
    MODERATOR(8),
    ADMIN(16)
}