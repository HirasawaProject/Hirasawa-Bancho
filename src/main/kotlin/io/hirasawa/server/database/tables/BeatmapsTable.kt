package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object BeatmapsTable: IntIdTable("beatmaps") {
    val mapsetId = integer("mapset_id").references(BeatmapsetsTable.id)
    val difficulty = varchar("difficulty", 255)
    val hash = varchar("hash", 32)
    val ranks = integer("ranks")
    val offset = float("offset")
}