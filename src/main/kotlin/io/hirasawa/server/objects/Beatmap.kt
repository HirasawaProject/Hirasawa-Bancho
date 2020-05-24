package io.hirasawa.server.objects

import io.hirasawa.server.Hirasawa

data class Beatmap(val id: Int, val mapsetId: Int, val difficulty: String, val hash: String, val ranks: Int,
                   val offset: Float) {
    val beatmapSet by lazy { Hirasawa.database.getBeatmapSet(mapsetId) }
}