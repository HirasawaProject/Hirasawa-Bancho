package io.hirasawa.server.bancho.objects

import io.hirasawa.server.bancho.enums.Mode

data class BanchoStatus(val status: Byte, val statusText: String, val beatmapChecksum: String, val mods: Int,
                        val mode: Mode, val beatmapId: Int){
    constructor(): this(0, "", "", 0, Mode.OSU, 0)
}