package io.hirasawa.server.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.BeatmapSetsTable
import io.hirasawa.server.enums.BeatmapStatus
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

data class BeatmapSet(val id: Int, val artist: String, val title: String, val status: BeatmapStatus, val osuId: Int,
                      val mapperName: String, val genreId: Int, val languageId: Int, val rating: Float) {
    val difficulties: ArrayList<Beatmap> by lazy {
        val difficulties = ArrayList<Beatmap>()
        val beatmapId = id
        transaction {
            BeatmapsTable.select { BeatmapsTable.mapsetId eq beatmapId }.forEach {
                difficulties.add(Beatmap(it))
            }
        }
        difficulties
    }

    constructor(result: ResultRow): this(result[BeatmapSetsTable.id].value, result[BeatmapSetsTable.artist],
        result[BeatmapSetsTable.title], BeatmapStatus.fromId(result[BeatmapSetsTable.status]),
        result[BeatmapSetsTable.osuId], result[BeatmapSetsTable.mapperName], result[BeatmapSetsTable.genreId],
        result[BeatmapSetsTable.languageId], result[BeatmapSetsTable.rating])
}