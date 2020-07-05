package io.hirasawa.server.routes

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.ScoresTable
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.Score
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route
import kotlinx.html.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class BeatmapRoute: Route {
    override fun handle(request: Request, response: Response) {
        val beatmapId = request.routeParameters["beatmap"]?.toInt() ?: return
        val mode = GameMode.values()[request.get["m"]?.toInt() ?: 0]

        val beatmap = Hirasawa.databaseToObject<Beatmap>(Beatmap::class, transaction {
            BeatmapsTable.select {
                BeatmapsTable.osuId eq beatmapId
            }.firstOrNull()
        })
        val beatmapset = beatmap?.beatmapSet

        response.writeRawHtml {
            head {
                if (beatmapset == null) {
                    title("Beatmap not found")
                } else {
                    title("${beatmapset.artist} - ${beatmapset.title}")
                }
            }

            body {
                if (beatmap == null) {
                    h2 {
                        b {
                            text("Beatmap not found!")
                        }
                    }
                } else {
                    a (href = "?m=0") {
                        text("osu")
                    }
                    text(" | ")
                    a (href = "?m=1") {
                        text("taiko")
                    }
                    text(" | ")
                    a (href = "?m=2") {
                        text("ctb")
                    }
                    text(" | ")
                    a (href = "?m=3") {
                        text("mania")
                    }

                    table {
                        tr {
                            th {
                                text("Rank")
                            }
                            th {
                                text("User")
                            }
                            th {
                                text("Score")
                            }
                            th {
                                text("Combo")
                            }
                            th {
                                text("FC")
                            }
                        }

                        transaction {
                            ScoresTable.select {
                                (ScoresTable.beatmapId eq beatmap.id) and (ScoresTable.gamemode eq mode.ordinal)
                            }.limit(50).sortedByDescending { ScoresTable.score }
                        }.forEach {
                            val score = Score(it)
                            tr {
                                td {
                                    text(score.rank)
                                }
                                td {
                                    text(score.user.username)
                                }
                                td {
                                    text(score.score)
                                }
                                td {
                                    text(score.combo)
                                }
                                td {
                                    text(if (score.fullCombo) "Yes" else "No")
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}