package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object BeatmapsTable: IntIdTable("beatmaps") {
    val osuId = integer("osu_id").uniqueIndex()
    val mapsetId = integer("beatmap_set_id").references(BeatmapSetsTable.id)
    val difficulty = varchar("difficulty", 255)
    val hash = varchar("hash", 32)
    val ranks = integer("ranks")
    val offset = float("offset")
    val totalLength = integer("total_length")
    val hitLength = integer("hit_length")
    val circleSize = float("circle_size")
    val overallDifficulty = float("overall_difficulty")
    val approachRate = float("approach_rate")
    val healthDrain = float("health_drain")
    val gamemode = integer("gamemode")
    val countNormal = integer("count_normal")
    val countSlider = integer("count_slider")
    val countSpinner = integer("count_spinner")
    val bpm = float("bpm")
    val hasStoryboard = bool("has_storyboard")
    val maxCombo = integer("max_combo")
    val playCount = integer("play_count")
    val passCount = integer("pass_count")
}