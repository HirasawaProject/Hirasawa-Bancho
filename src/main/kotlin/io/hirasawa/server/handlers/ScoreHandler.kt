package io.hirasawa.server.handlers

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.UsersTable
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.Score
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ScoreHandler(encodedScore: String) {
    private val separator = ':'

    var fileChecksum: String
    var username: String
    var scoreChecksum: String
    var count300: Int
    var count100: Int
    var count50: Int
    var countGeki: Int
    var countKatu: Int
    var countMiss: Int
    var countScore: Int
    var combo: Int
    var fullCombo: Boolean
    var ranking: String
    var mods: Int
    var pass: Boolean
    var mode: GameMode
    var date: String
    var version: String

    var score: Score? = null

    init {
        val scoreArray = encodedScore.split(separator)
        fileChecksum = scoreArray[0]
        username = scoreArray[1].trim()
        scoreChecksum = scoreArray[2]
        count300 = scoreArray[3].toInt()
        count100 = scoreArray[4].toInt()
        count50 = scoreArray[5].toInt()
        countGeki = scoreArray[6].toInt()
        countKatu = scoreArray[7].toInt()
        countMiss = scoreArray[8].toInt()
        countScore = scoreArray[9].toInt()
        combo = scoreArray[10].toInt()
        fullCombo = scoreArray[11] == "True"
        ranking = scoreArray[12]
        mods = scoreArray[13].toInt()
        pass = scoreArray[14] == "True"
        mode = GameMode.values()[scoreArray[15].toInt()]
        date = scoreArray[16]
        version = scoreArray[17]

        val user = Hirasawa.databaseToObject<BanchoUser>(BanchoUser::class, transaction {
            UsersTable.select {
                UsersTable.username eq username
            }.firstOrNull()
        })
        val beatmap = Hirasawa.databaseToObject<Beatmap>(Beatmap::class, transaction {
            BeatmapsTable.select {
                BeatmapsTable.hash eq fileChecksum
            }.firstOrNull()
        })

        if (user != null && beatmap != null) {
            score = Score(-1, user, countScore, combo, count50, count100, count300, countMiss, countKatu, countGeki,
                fullCombo, mods, -1, mode, -1, beatmap.id, calculateAccuracy())
        }
    }

    private fun calculateAccuracy(): Float {
        // Ported from old closed-source nosue! project keeping variable names the same
        // TODO redo with better readability
        var accuracy = 0F
        when(mode) {
            GameMode.OSU -> {
                val totalPoints = (count300 * 300) + (count100 * 100) + (count50 * 50)
                val totalHits = count300 + count100 + count50 + countMiss

                accuracy = totalPoints / (totalHits * 300F)
            }
            GameMode.TAIKO -> {
                val totalPoints = ((count100 * 0.5F) + count300) * 300
                val totalHits = count300 + count100 + countMiss

                accuracy = totalPoints / (totalHits * 300F)
            }
            GameMode.CATCH_THE_BEAT -> {
                val totalPoints = count300 + count100 + count50
                val totalHits = count300 + count100 + count50 + countMiss

                accuracy = totalPoints / (totalHits * 300F)
            }
            GameMode.MANIA -> {
                val totalPoints = (count300 * 300F) + (count100 * 100F) + (count50 * 50F) + (countGeki * 300)
                    + (countKatu * 200)
                val totalHits = count300 + count100 + count50 + countGeki + countKatu + countMiss

                accuracy = totalPoints / totalHits
            }
        }

        return accuracy
    }
}