package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when a player updates their gamemode
 */
class BanchoUserModeChangeEvent(val banchoUser: BanchoUser, val gameMode: GameMode): HirasawaEvent, Cancelable()