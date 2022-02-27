package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.enums.QuitReason
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent
import io.hirasawa.server.plugin.event.bancho.enums.BanchoLoginCancelReason

/**
 * @param requester The user that caused the event
 * @param requested The IDs the user requested
 */
data class BanchoUserPresenceRequestEvent(val requester: BanchoUser, val requested: ArrayList<Int>): HirasawaEvent<BanchoUserPresenceRequestEvent>, Cancelable()