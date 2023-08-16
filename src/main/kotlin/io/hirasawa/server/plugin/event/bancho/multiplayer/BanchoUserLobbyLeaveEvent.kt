package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user stops looking at the multiplayer lobby screen
 */
class BanchoUserLobbyLeaveEvent(val user: BanchoUser): HirasawaEvent<BanchoUserLobbyLeaveEvent>