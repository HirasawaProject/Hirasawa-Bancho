package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent
import io.hirasawa.server.plugin.event.bancho.enums.BanchoLoginCancelReason

/**
 * User login event to Bancho
 * This occurs after authentication and before the client is sent the response
 *
 */
data class BanchoUserLoginEvent(val user: User,var cancelReason: BanchoLoginCancelReason):
        HirasawaEvent<BanchoUserLoginEvent>, Cancelable() {
    /**
     * Cancels event with reason populated
     */
    fun cancelLogin(reason: BanchoLoginCancelReason) {
        cancelReason = reason
        isCancelled = true
    }
}