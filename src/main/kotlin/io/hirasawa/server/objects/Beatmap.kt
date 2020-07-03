package io.hirasawa.server.objects

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.BeatmapsetsTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

data class Beatmap(var id: Int, var mapsetId: Int, var difficulty: String, var hash: String, var ranks: Int,
                   var offset: Float) {

    constructor(result: ResultRow): this(result[BeatmapsTable.id].value, result[BeatmapsTable.mapsetId],
        result[BeatmapsTable.difficulty], result[BeatmapsTable.hash], result[BeatmapsTable.ranks],
        result[BeatmapsTable.offset])
    val beatmapSet by lazy {
        BeatmapSet(transaction {
            BeatmapsetsTable.select {
                BeatmapsetsTable.id eq mapsetId
            }.first()
        })
    }
}