package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Called when a match password has been changed
 */
class BanchoMatchPasswordChangeEvent(val match: MultiplayerMatch,
                                     val from: String,
                                     val to: String): HirasawaEvent<BanchoMatchPasswordChangeEvent>, Cancelable()