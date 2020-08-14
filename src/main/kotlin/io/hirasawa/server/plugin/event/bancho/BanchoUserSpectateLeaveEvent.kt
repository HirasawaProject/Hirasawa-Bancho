package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when player stops spectating another player
 */
class BanchoUserSpectateLeaveEvent(val spectator: BanchoUser, val spectatee: BanchoUser): HirasawaEvent