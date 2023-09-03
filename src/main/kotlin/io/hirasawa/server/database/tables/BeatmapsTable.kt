package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable
import org.jetbrains.exposed.dao.id.IntIdTable

object BeatmapsTable: LaravelTable("beatmaps") {
    val osuId = integer("osu_id").uniqueIndex()
    val mapsetId = integer("beatmap_set_id").references(BeatmapSetsTable.id)
    val difficultyName = varchar("difficulty_name", 255)
    val hash = varchar("hash", 32)
    val offset = float("`offset`")
    val totalLength = integer("total_length")
    val hitLength = integer("hit_length")
    val circleSize = float("circle_size")
    val overallDifficulty = float("overall_difficulty")
    val approachRate = float("approach_rate")
    val healthDrain = float("health_drain")
    val mode = integer("mode")
    val countNormal = integer("count_normal")
    val countSlider = integer("count_slider")
    val countSpinner = integer("count_spinner")
    val bpm = float("bpm")
    val hasStoryboard = bool("has_storyboard")
    val maxCombo = integer("max_combo")
}