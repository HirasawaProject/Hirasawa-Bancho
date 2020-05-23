package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User

class LoginPermissionsPacket(val user: BanchoUser): BanchoPacket(BanchoPacketType.BANCHO_LOGIN_PERMISSIONS) {
    init {
        writer.writeInt(user.clientPermissions.toInt())
    }
}