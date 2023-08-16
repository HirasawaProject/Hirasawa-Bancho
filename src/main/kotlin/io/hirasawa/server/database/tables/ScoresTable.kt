package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable
import org.jetbrains.exposed.dao.id.IntIdTable

object ScoresTable: LaravelTable("scores") {
    val userId = integer("user_id").references(UsersTable.id)
    val score = integer("score")
    val combo = integer("combo")
    val count50 = integer("count_50")
    val count100 = integer("count_100")
    val count300 = integer("count_300")
    val countMiss = integer("count_miss")
    val countKatu = integer("count_katu")
    val countGeki = integer("count_geki")
    val fullCombo = bool("full_combo")
    val mods = integer("mods")
    val beatmapId = integer("beatmap_id").references(BeatmapsTable.id)
    val mode = integer("mode")
    val rank = integer("rank")
    val accuracy = float("accuracy")
    val hasReplay = bool("has_replay")
}