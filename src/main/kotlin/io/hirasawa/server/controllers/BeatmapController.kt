package io.hirasawa.server.controllers

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.database.tables.BeatmapsTable
import io.hirasawa.server.database.tables.BeatmapsetsTable
import io.hirasawa.server.database.tables.ScoresTable
import io.hirasawa.server.handlers.OsuSearchBeatmapHandler
import io.hirasawa.server.mvc.Controller
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.views.BeatmapView
import io.hirasawa.server.webserver.enums.ContentType
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.exceptions.HttpException
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.respondable.HttpRespondable
import io.hirasawa.server.webserver.respondable.RedirectRespondable
import io.hirasawa.server.webserver.respondable.ViewRespondable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class BeatmapController: Controller {
    fun index(request: Request): HttpRespondable {
        val beatmapId = request.routeParameters["beatmap"]?.toInt() ?: throw HttpException(HttpStatus.INTERNAL_SERVER_ERROR)
        val mode = GameMode.values()[request.get["m"]?.toInt() ?: 0]

        val beatmap = Hirasawa.databaseToObject<Beatmap>(Beatmap::class, transaction {
            BeatmapsTable.select {
                BeatmapsTable.osuId eq beatmapId
            }.firstOrNull()
        })
        val beatmapset = beatmap?.beatmapSet
        val scores = if (beatmap != null ) transaction {
            ScoresTable.select {
                (ScoresTable.beatmapId eq beatmap.id) and (ScoresTable.gamemode eq mode.ordinal)
            }.limit(50).sortedByDescending { ScoresTable.score }
        }.toList() else listOf()
        return ViewRespondable(BeatmapView(beatmapset, beatmap, scores))
    }

    fun download(request: Request): HttpRespondable {
        return RedirectRespondable("https://chimu.moe/d/${request.routeParameters["beatmap"]}")
    }

    fun search(request: Request): HttpRespondable {
        if (request.headers["user-agent"] != "osu!") {
            throw HttpException(HttpStatus.FORBIDDEN)
        }

        return OsuDirectSearchRespondable()
    }

    class OsuDirectSearchRespondable: HttpRespondable() {
        override fun respond(request: Request, response: Response) {
            response.headers[HttpHeader.CONTENT_TYPE] = ContentType.TEXT_PLAN

            val username = request.get["u"] ?: return
            val password = request.get["h"] ?: return
            val query = request.get["q"]?.replace("+", " ") ?: return
            val requestType = RequestType.fromId(request.get["r"]?.toInt() ?: return) // TODO support request type
            val mode = request.get["m"] ?: return // TODO support gamemode
            val page = request.get["p"]?.toInt() ?: 1


            if (!Hirasawa.authenticate(username, password)) {
                return
            }

            var beatmapSort = BeatmapsetsTable.id
            var beatmapQuery = ""

            when (query) {
                "Newest" -> {
                    beatmapSort = BeatmapsetsTable.id
                }
                "Top Rated" -> {
                    beatmapSort = BeatmapsetsTable.id
                }
                "Most Played" -> {
                    beatmapSort = BeatmapsetsTable.id
                }
                else -> {
                    beatmapQuery = query
                }
            }

            val beatmaps = ArrayList<BeatmapSet>()

            transaction {
                BeatmapsetsTable.select {
                    (BeatmapsetsTable.title like "%$beatmapQuery%") or (BeatmapsetsTable.artist like "%$beatmapQuery%")
                }.limit(page * 100, ((page + 1) * 100).toLong()).sortedBy { beatmapSort }.forEach {
                    beatmaps.add(BeatmapSet(it))
                }
            }

            response.writeText("${beatmaps.size}\n")
            for (beatmap in beatmaps) {
                OsuSearchBeatmapHandler(beatmap).write(response.outputStream)
            }
        }

        private enum class RequestType(val id: Int) {
            ALL(4),
            RANKED(0),
            RANKED_PLAYED(7),
            LOVED(8),
            QUALIFIED(3),
            PENDING(2),
            GRAVEYARD(5);

            companion object {
                private val map = values().associateBy(RequestType::id)
                fun fromId(type: Int) = map[type]
            }
        }

    }
}