package io.hirasawa.server.webserver.internalroutes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.database.MemoryDatabase
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.routes.web.OsuOsz2GetScoresRoute
import io.hirasawa.server.webserver.Helper
import io.hirasawa.server.webserver.Helper.Companion.createBeatmap
import io.hirasawa.server.webserver.Helper.Companion.createBeatmapSet
import io.hirasawa.server.webserver.Helper.Companion.createScore
import io.hirasawa.server.webserver.Helper.Companion.createUser
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import kotlin.collections.HashMap

class GetScoresRouteTests {
    val database = MemoryDatabase()
    init {
        Hirasawa.initDatabase(database)
    }

    fun requestRoute(username: String, password: String, gameMode: GameMode, beatmapHash: String,
                     responseBuffer: ByteArrayOutputStream): Response {
        val route = OsuOsz2GetScoresRoute()

        val params = HashMap<String, String>()
        params["us"] = username
        params["ha"] = password
        params["m"] = gameMode.ordinal.toString()
        params["c"] = beatmapHash

        val headers = MutableHeaders(HashMap<String, String>())
        headers[HttpHeader.USER_AGENT] = "osu!"

        val request = Request(
            UrlSegment("", "/web/osu-osz2-getscores.php", params), HttpMethod.GET, headers.makeImmutable(),
            ByteArrayInputStream(ByteArray(0))
        )
        val response = Response(HttpStatus.OK, DataOutputStream(responseBuffer),
            Hirasawa.webserver.getDefaultHeaders())

        route.handle(request, response)

        return response
    }

    @Test
    fun testDoesScoresRouteShowCorrectStatus() {
        database.users.add(Helper.createUser(1, "CurrentStatus"))

        val responseBuffer = ByteArrayOutputStream()
        requestRoute("CurrentStatus", "", GameMode.OSU, "onethatdoesn'texist", responseBuffer)

        val responseString = String(responseBuffer.toByteArray())

        assertEquals("-1|false", responseString)
    }

    @Test
    fun testDoesLeaderboardShowOurScore() {
        val user = createUser(1, "OurScore")
        database.users.add(user)
        database.scores.add(createScore(1, user, 1000, 1, 1, GameMode.OSU))
        database.beatmaps.add(createBeatmap(1, 1, "foo", 1))
        database.beatmapSets.add(createBeatmapSet(1, "Artist", "Song", BeatmapStatus.RANKED))

        val responseBuffer = ByteArrayOutputStream()
        requestRoute("OurScore", "", GameMode.OSU, "foo", responseBuffer)

        val responseString = String(responseBuffer.toByteArray())

        assert(responseString.contains("1|OurScore|1000|100|50|100|300|0|10|10|1|0|1|1|0|1")) // User score
        assert(responseString.contains("1|OurScore|1000|100|50|100|300|0|10|10|1|0|1|1|0|0")) // Leaderboard score
    }

    @Test
    fun testDoesLeaderboardNotShowTaikoFromOsu() {
        database.users.add(createUser(1, "NotOtherMods"))


        database.beatmaps.add(createBeatmap(1, 1, "foo", 1))
        database.beatmapSets.add(createBeatmapSet(1, "Artist", "Song", BeatmapStatus.RANKED))


        val standardUser = createUser(2, "osu")
        database.scores.add(createScore(1, standardUser, 1000, 1, 1, GameMode.OSU))

        val taikoUser = createUser(3, "Taiko")
        database.scores.add(createScore(2, taikoUser, 1000, 1, 1, GameMode.TAIKO))

        val standardBuffer = ByteArrayOutputStream()
        requestRoute("NotOtherMods", "", GameMode.OSU, "foo", standardBuffer)

        val responseString = String(standardBuffer.toByteArray())

        println(responseString)

        assert(responseString.contains("osu"))
        assertFalse(responseString.contains("Taiko"))
    }

    @Test
    fun testDoesLeaderboardNotShowOsuFromTaiko() {
        database.users.add(createUser(1, "NotOtherMods"))


        database.beatmaps.add(createBeatmap(1, 1, "foo", 1))
        database.beatmapSets.add(createBeatmapSet(1, "Artist", "Song", BeatmapStatus.RANKED))


        val standardUser = createUser(2, "osu")
        database.scores.add(createScore(1, standardUser, 1000, 1, 1, GameMode.OSU))

        val taikoUser = createUser(3, "Taiko")
        database.scores.add(createScore(2, taikoUser, 1000, 1, 1, GameMode.TAIKO))

        val standardBuffer = ByteArrayOutputStream()
        requestRoute("NotOtherMods", "", GameMode.TAIKO, "foo", standardBuffer)

        val responseString = String(standardBuffer.toByteArray())

        assert(responseString.contains("Taiko"))
        assertFalse(responseString.contains("osu"))
    }
}