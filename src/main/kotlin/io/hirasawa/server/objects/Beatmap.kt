package io.hirasawa.server.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.BeatmapsetsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

data class Beatmap(var id: Int, var mapsetId: Int, var difficulty: String, var hash: String, var ranks: Int,
                   var offset: Float, val osuId: Int, val totalLength: Int, val hitLength: Int, val diffSize: Float,
                   val diffOverall: Float, val diffApproach: Float, val diffDrain: Float, val mode: GameMode,
                   val countNormal: Int, val countSlider: Int, val countSpinner: Int, val bpm: Float,
                   val hasStoryboard: Boolean, val maxCombo: Int, val playCount: Int, val passCount: Int) {

    constructor(result: ResultRow): this(result[BeatmapsTable.id].value, result[BeatmapsTable.mapsetId],
        result[BeatmapsTable.difficulty], result[BeatmapsTable.hash], result[BeatmapsTable.ranks],
        result[BeatmapsTable.offset], result[BeatmapsTable.osuId], result[BeatmapsTable.totalLength],
        result[BeatmapsTable.hitLength], result[BeatmapsTable.diffSize], result[BeatmapsTable.diffOverall],
        result[BeatmapsTable.diffApproach], result[BeatmapsTable.diffDrain],
        GameMode.values()[result[BeatmapsTable.mode]], result[BeatmapsTable.countNormal],
        result[BeatmapsTable.countSlider], result[BeatmapsTable.countSpinner], result[BeatmapsTable.bpm],
        result[BeatmapsTable.storyboard], result[BeatmapsTable.maxCombo], result[BeatmapsTable.playCount],
        result[BeatmapsTable.passCount])

    val beatmapSet by lazy {
        BeatmapSet(transaction {
            BeatmapsetsTable.select {
                BeatmapsetsTable.osuId eq mapsetId
            }.first()
        })
    }
}