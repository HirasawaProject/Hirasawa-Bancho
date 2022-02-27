package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.enums.QuitReason
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.plugin.event.HirasawaEvent
import io.hirasawa.server.plugin.event.bancho.enums.BanchoLoginCancelReason

/**
 * User quit event to Bancho
 * This occurs just before the user is about to quit
 *
 */
data class BanchoUserQuitEvent(val user: BanchoUser, val reason: QuitReason): HirasawaEvent<BanchoUserQuitEvent>