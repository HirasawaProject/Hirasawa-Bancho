package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when player moves from spectating one player to another
 */
class BanchoUserSpectateSwitchEvent(val spectator: BanchoUser, val oldSpectatee: BanchoUser,
                                    val newSpectatee: BanchoUser): HirasawaEvent<BanchoUserSpectateSwitchEvent>