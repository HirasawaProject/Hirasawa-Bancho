package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable

object BeatmapSetsTable: LaravelTable("beatmap_sets") {
    val osuId = integer("osu_id")
    val artist = varchar("artist", 255)
    val title = varchar("title", 255)
    val status = integer("status")
    val mapperName = varchar("mapper_name", 255)
    val genreId = integer("genre_id")
    val languageId = integer("language_id")
    val rating = float("rating")
}