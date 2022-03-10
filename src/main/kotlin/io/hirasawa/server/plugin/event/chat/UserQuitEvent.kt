package io.hirasawa.server.plugin.event.chat

import io.hirasawa.server.bancho.enums.QuitReason
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * User quit event on Bancho and IRC
 * This occurs just before the user is about to quit
 *
 */
data class UserQuitEvent(val user: User, val reason: QuitReason): HirasawaEvent<UserQuitEvent>