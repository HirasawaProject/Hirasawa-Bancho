package io.hirasawa.server.plugin.event.web

import io.hirasawa.server.objects.Score
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when player submits a score
 */
class ScoreSubmitEvent(val score: Score): HirasawaEvent<ScoreSubmitEvent>, Cancelable()