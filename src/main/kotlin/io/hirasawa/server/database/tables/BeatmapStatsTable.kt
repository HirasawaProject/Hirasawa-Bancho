package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable

object BeatmapStatsTable: LaravelTable("beatmap_stats") {
    val beatmapId = integer("beatmap_id").references(BeatmapsTable.id)
    val mode = integer("mode")
    val playCount = integer("play_count")
    val passCount = integer("pass_count")
}