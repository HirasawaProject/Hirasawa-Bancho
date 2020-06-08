package io.hirasawa.server.objects

import io.hirasawa.server.Hirasawa

data class Beatmap(var id: Int, var mapsetId: Int, var difficulty: String, var hash: String, var ranks: Int,
                   var offset: Float) {
    val beatmapSet by lazy { Hirasawa.database.getBeatmapSet(mapsetId) }
}