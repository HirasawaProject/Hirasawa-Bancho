package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when player starts spectating another player
 */
class BanchoUserSpectateJoinEvent(val spectator: BanchoUser, val spectatee: BanchoUser): HirasawaEvent