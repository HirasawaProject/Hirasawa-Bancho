package io.hirasawa.server.plugin.event.bancho.multiplayer

import io.hirasawa.server.bancho.enums.MatchSlotStatus
import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event that gets called whenever a slot is toggled between locked and unlocked
 */
class BanchoMatchToggleSlotEvent(val match: MultiplayerMatch,
                                 val slot: Int,
                                 val from: MatchSlotStatus,
                                 val to: MatchSlotStatus): HirasawaEvent<BanchoMatchToggleSlotEvent>, Cancelable()