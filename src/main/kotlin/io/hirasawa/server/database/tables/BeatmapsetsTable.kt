package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object BeatmapsetsTable: IntIdTable("beatmapsets") {
    val artist = varchar("artist", 255)
    val title = varchar("title", 255)
    val status = integer("status")
}