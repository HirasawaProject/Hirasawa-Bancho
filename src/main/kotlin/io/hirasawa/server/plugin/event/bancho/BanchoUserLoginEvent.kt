package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.plugin.event.HirasawaEvent
import io.hirasawa.server.plugin.event.bancho.enums.BanchoLoginCancelReason

/**
 * User login event to Bancho
 * This occurs after authentication and before the client is sent the response
 *
 * This may not be a Bancho user as Bancho WILL accept connections from osu! and IRC which handle things differently
 */
data class BanchoUserLoginEvent(val user: User): HirasawaEvent {
    var cancelReason = BanchoLoginCancelReason.NOT_CANCELLED
    fun cancelLogin(reason: BanchoLoginCancelReason) {
        cancelReason = reason
    }
}