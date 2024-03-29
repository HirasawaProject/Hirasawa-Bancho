package io.hirasawa.server.config

import io.hirasawa.server.enums.DefaultRankingState
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.ChatChannelMetadata
import io.hirasawa.server.database.DatabaseCredentials
import io.hirasawa.server.enums.Mod
import io.hirasawa.server.objects.Mods

data class HirasawaConfig (val httpPort: Int, val ircPort: Int, val database: DatabaseCredentials,
                           val banchoUserTimeout: Int, val ircUserTimeout: Int, val banchoBotId: Int,
                           val domain: String, val blockedMods: List<Mod>, val osuApiKey: String,
                           val ircWelcomeMessage: String, val ircMotd: List<String>, val multiplayerFreeMods: Mods,
                           val defaultRankingState: DefaultRankingState
) {
    constructor(): this(
        8080,
        6667,
        DatabaseCredentials(),
        60,
        60,
        3,
        "localhost",
        listOf(Mod.AUTOPLAY, Mod.RELAX, Mod.AUTOPILOT, Mod.CINEMA, Mod.TARGET, Mod.SCORE_V2),
        "",
        "Welcome to Hirasawa!",
        listOf(
            "This is an example of a MOTD for the IRC server",
            "set this to whatever you want"
        ),
        Mods(
            arrayListOf(Mod.NO_FAIL, Mod.EASY, Mod.HIDDEN, Mod.HARD_ROCK, Mod.SUDDEN_DEATH, Mod.FLASHLIGHT, Mod.RELAX,
            Mod.AUTOPILOT, Mod.SPUN_OUT, Mod.KEY_MODS)
        ),
        DefaultRankingState.MATCH_OSU
    )
}