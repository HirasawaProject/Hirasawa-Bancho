package io.hirasawa.server.routes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.database.tables.ScoresTable
import io.hirasawa.server.database.tables.UserStatsTable
import io.hirasawa.server.database.tables.UsersTable
import io.hirasawa.server.enums.Mod
import io.hirasawa.server.handlers.ScoreHandler
import io.hirasawa.server.objects.Score
import io.hirasawa.server.plugin.event.web.ScoreSubmitEvent
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.internalroutes.errors.RouteForbidden
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route
import org.bouncycastle.crypto.InvalidCipherTextException
import org.bouncycastle.crypto.engines.RijndaelEngine
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.paddings.ZeroBytePadding
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.util.encoders.Base64
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception
import java.time.Instant


class OsuSubmitModular: Route {
    override fun handle(request: Request, response: Response) {
        if (request.headers[HttpHeader.USER_AGENT] != "osu!") {
            RouteForbidden().handle(request, response)
            return
        }

        val iv = Base64.decode(request.post["iv"])
        val encodedScore = Base64.decode(request.post["score"])
        val key = "osu!-scoreburgr---------${request.post["osuver"]}".toByteArray()

        val decryptedScore = decrypt(key, iv, encodedScore)

        val handler = ScoreHandler(decryptedScore)

        if (handler.score == null) {
            response.writeText("error: pass")
            return
        }

        if (Hirasawa.authenticate(handler.username, request.post["pass"] ?: "")) {
            val score = handler.score!!

            val event = ScoreSubmitEvent(score)
            Hirasawa.eventHandler.callEvent(event)

            if (event.isCancelled) {
                return
            }

            val mods = Mod.idToModArray(score.mods)
            for (mod in Hirasawa.config.blockedMods) {
                if (mod in mods) {
                    return
                }
            }

            val topScore = Hirasawa.databaseToObject<Score>(Score::class, transaction {
                (ScoresTable innerJoin UsersTable).select {
                    (ScoresTable.beatmapId eq score.beatmapId) and (ScoresTable.gamemode eq score.gameMode.ordinal) and
                            (ScoresTable.userId eq score.user.id)
                }.firstOrNull()
            })

            score.timestamp = Instant.now().epochSecond.toInt()

            if (topScore != null) {
                if (score.score <= topScore.score) {
                    // Does this score beat our last? If not quit
                    return
                }

                transaction {
                    ScoresTable.deleteWhere(1) { ScoresTable.id eq score.id }
                }
            }

            Hirasawa.pipeline.queue(Runnable {
                transaction {
                    ScoresTable.insert {
                        it[ScoresTable.userId] = score.user.id
                        it[ScoresTable.score] = score.score
                        it[ScoresTable.combo] = score.combo
                        it[ScoresTable.count50] = score.count50
                        it[ScoresTable.count100] = score.count100
                        it[ScoresTable.count300] = score.count300
                        it[ScoresTable.countMiss] = score.countMiss
                        it[ScoresTable.countKatu] = score.countKatu
                        it[ScoresTable.countGeki] = score.countGeki
                        it[ScoresTable.fullCombo] = score.fullCombo
                        it[ScoresTable.mods] = score.mods
                        it[ScoresTable.timestamp] = score.timestamp
                        it[ScoresTable.beatmapId] = score.beatmapId
                        it[ScoresTable.gamemode] = score.gameMode.ordinal
                        it[ScoresTable.rank] = score.rank
                        it[ScoresTable.accuracy] = score.accuracy
                    }
                }
                Hirasawa.processLeaderboard(score.beatmap, score.gameMode)

                var rankedScore = 0L
                var scoreCount = 0
                var accuracyTotal = 0F
                transaction {
                    ScoresTable.select {
                        (ScoresTable.gamemode eq score.gameMode.ordinal) and (ScoresTable.userId eq score.user.id)
                    }.forEach {
                        val userScore = Score(it)
                        scoreCount++
                        rankedScore += userScore.score
                        accuracyTotal += userScore.accuracy
                    }
                }

                val accuracy = accuracyTotal / scoreCount

                val userStats = UserStats(transaction {
                    UserStatsTable.select {
                        (UserStatsTable.userId eq score.user.id) and
                                (UserStatsTable.gamemode eq score.gameMode.ordinal)
                    }.first()
                })

                userStats.totalScore += score.score
                userStats.rankedScore = rankedScore
                userStats.accuracy = accuracy
                userStats.playcount++

                UserStatsTable.update({ (UserStatsTable.userId eq userStats.userId) and
                        (UserStatsTable.gamemode eq userStats.gameMode.ordinal) }) {
                    it[UserStatsTable.totalScore] = userStats.totalScore
                    it[UserStatsTable.rankedScore] = userStats.rankedScore
                    it[UserStatsTable.accuracy] = userStats.accuracy
                    it[UserStatsTable.playcount] = userStats.playcount
                }


                Hirasawa.processGlobalLeaderboard(score.gameMode)

            })


        } else {
            response.writeText("error: pass")
        }
    }



    private fun decrypt(key: ByteArray, iv: ByteArray, data: ByteArray): String {
        val aes = PaddedBufferedBlockCipher(CBCBlockCipher(RijndaelEngine(256)), ZeroBytePadding())
        aes.init(false, ParametersWithIV(KeyParameter(key), iv))
        return String(cipherData(aes, data)!!)
    }

    /**
     * Stolen from https://stackoverflow.com/questions/8083144/how-to-encrypt-or-decrypt-with-rijndael-and-a-block-size-of-256-bits
     */
    @Throws(InvalidCipherTextException::class)
    private fun cipherData(cipher: PaddedBufferedBlockCipher, data: ByteArray): ByteArray? {
        val minSize = cipher.getOutputSize(data.size)
        val outBuf = ByteArray(minSize)
        val length1 = cipher.processBytes(data, 0, data.size, outBuf, 0)
        val length2 = cipher.doFinal(outBuf, length1)
        val actualLength = length1 + length2
        val cipherArray = ByteArray(actualLength)
        for (x in 0 until actualLength) {
            cipherArray[x] = outBuf[x]
        }
        return cipherArray
    }
}