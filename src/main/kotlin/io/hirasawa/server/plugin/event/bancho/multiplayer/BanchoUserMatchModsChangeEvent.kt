package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.objects.Mods
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a user changes mods, this is before processing freemods
 */
class BanchoUserMatchModsChangeEvent(val user: BanchoUser,
                                     val match: MultiplayerMatch,
                                     val from: Mods,
                                     val to: Mods): HirasawaEvent<BanchoUserMatchModsChangeEvent>, Cancelable()