package io.hirasawa.server.routes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.database.tables.BeatmapSetsTable
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.handlers.OsuSearchBeatmapHandler
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.plugin.HirasawaPlugin
import io.hirasawa.server.webserver.enums.ContentType
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.internalroutes.errors.RouteForbidden
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class OsuSearchRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.headers[HttpHeader.CONTENT_TYPE] = ContentType.TEXT_PLAN
        if (request.headers["user-agent"] != "osu!") {
            RouteForbidden().handle(request, response)
            return
        }

        val username = request.get["u"] ?: return
        val password = request.get["h"] ?: return
        val query = request.get["q"]?.replace("+", " ") ?: return
        val requestType = RequestType.fromId(request.get["r"]?.toInt() ?: return) // TODO support request type
        val mode = request.get["m"] ?: return // TODO support gamemode
        val page = request.get["p"]?.toInt() ?: 1


        if (!Hirasawa.authenticate(username, password)) {
            return
        }

        var beatmapSort = BeatmapSetsTable.id
        var beatmapQuery = ""

        when (query) {
            "Newest" -> {
                beatmapSort = BeatmapSetsTable.id
            }
            "Top Rated" -> {
                beatmapSort = BeatmapSetsTable.id
            }
            "Most Played" -> {
                beatmapSort = BeatmapSetsTable.id
            }
            else -> {
                beatmapQuery = query
            }
        }

        val beatmaps = ArrayList<BeatmapSet>()

        transaction {
            BeatmapSetsTable.select {
                (BeatmapSetsTable.title like "%$beatmapQuery%") or (BeatmapSetsTable.artist like "%$beatmapQuery%")
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
            private val map = RequestType.values().associateBy(RequestType::id)
            fun fromId(type: Int) = map[type]
        }
    }
}
