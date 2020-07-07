package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.IntIdTable

object ScoresTable: IntIdTable("scores") {
    val userId = integer("user_id").references(UsersTable.id)
    val score = integer("score")
    val combo = integer("combo")
    val count50 = integer("count50")
    val count100 = integer("count100")
    val count300 = integer("count300")
    val countMiss = integer("count_miss")
    val countKatu = integer("count_katu")
    val countGeki = integer("count_geki")
    val fullCombo = bool("full_combo")
    val mods = integer("mods")
    val timestamp = integer("timestamp")
    val beatmapId = integer("beatmap_id").references(BeatmapsTable.id)
    val gamemode = integer("gamemode")
    val rank = integer("rank")
    val accuracy = float("accuracy")
}