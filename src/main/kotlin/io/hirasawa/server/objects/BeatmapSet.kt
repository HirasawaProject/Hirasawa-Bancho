package io.hirasawa.server.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.BeatmapsetsTable
import io.hirasawa.server.enums.BeatmapStatus
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

data class BeatmapSet(val id: Int, val artist: String, val title: String, val status: BeatmapStatus, val osuId: Int,
                      val mapperName: String, val genreId: Int, val languageId: Int, val rating: Float) {
    val difficulties: ArrayList<Beatmap> by lazy {
        val difficulties = ArrayList<Beatmap>()
        transaction {
            BeatmapsTable.select { BeatmapsTable.mapsetId eq id }.forEach {
                difficulties.add(Beatmap(it))
            }
        }
        difficulties
    }

    constructor(result: ResultRow): this(result[BeatmapsetsTable.id].value, result[BeatmapsetsTable.artist],
        result[BeatmapsetsTable.title], BeatmapStatus.fromId(result[BeatmapsetsTable.status]),
        result[BeatmapsetsTable.osuId], result[BeatmapsetsTable.mapperName], result[BeatmapsetsTable.genreId],
        result[BeatmapsetsTable.languageId], result[BeatmapsetsTable.rating])
}