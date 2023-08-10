package io.hirasawa.server.objects

import io.hirasawa.server.bancho.enums.Mode
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.ScoresTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

data class Score(var id: Int, var user: User, var score: Int, var combo: Int, var count50: Int, var count100: Int,
                 var count300: Int, var countMiss: Int, var countKatu: Int, var countGeki: Int, var fullCombo: Boolean,
                 var mods: Int, var mode: Mode, var rank: Int, var beatmapId: Int, var accuracy: Float,
                 var hasReplay: Boolean) {

    constructor(result: ResultRow): this(result[ScoresTable.id].value, BanchoUser(result), result[ScoresTable.score],
        result[ScoresTable.combo], result[ScoresTable.count50], result[ScoresTable.count100],
        result[ScoresTable.count300], result[ScoresTable.countMiss], result[ScoresTable.countKatu],
        result[ScoresTable.countGeki], result[ScoresTable.fullCombo], result[ScoresTable.mods],
        Mode.values()[result[ScoresTable.mode]], result[ScoresTable.rank], result[ScoresTable.beatmapId],
        result[ScoresTable.accuracy], result[ScoresTable.hasReplay])

    val beatmap: Beatmap by lazy {
        Beatmap(transaction {
            BeatmapsTable.select {
                BeatmapsTable.osuId eq beatmapId
            }.first()
        })
    }
}