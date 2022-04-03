package io.hirasawa.server.views

import io.hirasawa.server.mvc.View
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.objects.Score
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import org.jetbrains.exposed.sql.ResultRow

class BeatmapView(private val beatmapset: BeatmapSet?,
                  private val beatmap: Beatmap?,
                  private val scores: List<ResultRow>): View {
    override fun render(): String {
        return createHTML(false).html {
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

                        scores.forEach {
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