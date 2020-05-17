package io.hirasawa.server.bancho.objects

import io.hirasawa.server.bancho.enums.GameMode

data class BanchoStatus(val status: Byte, val statusText: String, val beatmapChecksum: String, val mods: Int,
                        val mode: GameMode, val beatmapId: Int){
    constructor(): this(0, "", "", 0, GameMode.OSU, 0)
}