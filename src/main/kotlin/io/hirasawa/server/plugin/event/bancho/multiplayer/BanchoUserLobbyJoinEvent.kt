package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user starts looking at the multiplayer lobby screen
 */
class BanchoUserLobbyJoinEvent(val user: BanchoUser): HirasawaEvent<BanchoUserLobbyJoinEvent>