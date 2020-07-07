package io.hirasawa.server.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.BeatmapsetsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

data class Beatmap(var id: Int, var mapsetId: Int, var difficulty: String, var hash: String, var ranks: Int,
                   var offset: Float, val osuId: Int, val totalLength: Int, val hitLength: Int, val circleSize: Float,
                   val overallDifficulty: Float, val approachRate: Float, val healthDrain: Float,
                   val gamemode: GameMode, val countNormal: Int, val countSlider: Int, val countSpinner: Int,
                   val bpm: Float, val hasStoryboard: Boolean, val maxCombo: Int, val playCount: Int,
                   val passCount: Int) {

    constructor(result: ResultRow): this(result[BeatmapsTable.id].value, result[BeatmapsTable.mapsetId],
        result[BeatmapsTable.difficulty], result[BeatmapsTable.hash], result[BeatmapsTable.ranks],
        result[BeatmapsTable.offset], result[BeatmapsTable.osuId], result[BeatmapsTable.totalLength],
        result[BeatmapsTable.hitLength], result[BeatmapsTable.circleSize], result[BeatmapsTable.overallDifficulty],
        result[BeatmapsTable.approachRate], result[BeatmapsTable.healthDrain],
        GameMode.values()[result[BeatmapsTable.gamemode]], result[BeatmapsTable.countNormal],
        result[BeatmapsTable.countSlider], result[BeatmapsTable.countSpinner], result[BeatmapsTable.bpm],
        result[BeatmapsTable.hasStoryboard], result[BeatmapsTable.maxCombo], result[BeatmapsTable.playCount],
        result[BeatmapsTable.passCount])

    val beatmapSet by lazy {
        BeatmapSet(transaction {
            BeatmapsetsTable.select {
                BeatmapsetsTable.id eq mapsetId
            }.first()
        })
    }
}