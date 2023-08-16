package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.objects.ReplayFrame
import io.hirasawa.server.bancho.objects.ScoreFrame
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when player sends spectate frames
 */
class BanchoUserSpectateFramesEvent(val user: BanchoUser, var replayFrames: ArrayList<ReplayFrame>, var action: Byte,
                                    var scoreFrame: ScoreFrame): HirasawaEvent<BanchoUserSpectateFramesEvent>, Cancelable()