package io.hirasawa.server.plugin.event.score

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.plugin.event.HirasawaEvent

class ClientLeaderboardFailEvent(val user: User, val beatmapHash: String, val gameMode: GameMode): HirasawaEvent<ClientLeaderboardFailEvent>