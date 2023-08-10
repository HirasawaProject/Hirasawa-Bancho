package io.hirasawa.server.objects

import io.hirasawa.server.bancho.enums.Mode
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.BeatmapSetsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

data class Beatmap(var id: Int, val osuId: Int, var mapsetId: Int, var difficulty: String, var hash: String,
                   var offset: Float, val totalLength: Int, val hitLength: Int, val circleSize: Float,
                   val overallDifficulty: Float, val approachRate: Float, val healthDrain: Float,
                   val mode: Mode, val countNormal: Int, val countSlider: Int, val countSpinner: Int,
                   val bpm: Float, val hasStoryboard: Boolean, val maxCombo: Int) {

    constructor(result: ResultRow): this(result[BeatmapsTable.id].value, result[BeatmapsTable.osuId],
        result[BeatmapsTable.mapsetId], result[BeatmapsTable.difficulty], result[BeatmapsTable.hash],
        result[BeatmapsTable.offset], result[BeatmapsTable.totalLength], result[BeatmapsTable.hitLength],
        result[BeatmapsTable.circleSize], result[BeatmapsTable.overallDifficulty], result[BeatmapsTable.approachRate],
        result[BeatmapsTable.healthDrain], Mode.values()[result[BeatmapsTable.mode]], result[BeatmapsTable.countNormal],
        result[BeatmapsTable.countSlider], result[BeatmapsTable.countSpinner], result[BeatmapsTable.bpm],
        result[BeatmapsTable.hasStoryboard], result[BeatmapsTable.maxCombo])

    val beatmapSet by lazy {
        BeatmapSet(transaction {
            BeatmapSetsTable.select {
                BeatmapSetsTable.id eq mapsetId
            }.first()
        })
    }
}