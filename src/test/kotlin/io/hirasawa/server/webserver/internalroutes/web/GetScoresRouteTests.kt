package io.hirasawa.server.webserver.internalroutes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
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
import java.lang.Exception
import kotlin.collections.HashMap

class GetScoresRouteTests {
    init {
        Hirasawa.initDatabase(memoryDatabase = true)
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
            HashMap(),
            ByteArrayInputStream(ByteArray(0)),
            "127.0.0.1"
        )
        val response = Response(HttpStatus.OK, DataOutputStream(responseBuffer),
            Hirasawa.webserver.getDefaultHeaders(), HashMap())

        route.handle(request, response)

        return response
    }

    @Test
    fun testDoesScoresRouteShowCorrectStatus() {
        val user = createUser("DSRSCS")

        val responseBuffer = ByteArrayOutputStream()
        requestRoute(user.username, "", GameMode.OSU, "onethatdoesn'texist", responseBuffer)

        val responseString = String(responseBuffer.toByteArray())

        assertEquals("-1|false", responseString)
    }

    @Test
    fun testDoesLeaderboardShowOurScore() {
        val user = createUser("DLSOS")

        val beatmapSet = createBeatmapSet("Artist", "Song", BeatmapStatus.RANKED)
        val beatmap = createBeatmap(beatmapSet.id, "DLSOS", 1)
        createScore(user, 1000, 1, beatmap.id, GameMode.OSU)

        val responseBuffer = ByteArrayOutputStream()
        requestRoute(user.username, "", GameMode.OSU, beatmap.hash, responseBuffer)

        val responseString = String(responseBuffer.toByteArray())

        assert(responseString.contains("1|${user.username}|1000|0|50|100|300|0|12|11|1|0|${user.id}|1|0|1")) // User score
        assert(responseString.contains("1|${user.username}|1000|0|50|100|300|0|12|11|1|0|${user.id}|1|0|0")) // Leaderboard score
    }

    @Test
    fun testDoesLeaderboardNotShowTaikoFromOsu() {
        val otherMods = createUser("DLNSTFO")

        val beatmapSet = createBeatmapSet("Artist", "Song", BeatmapStatus.RANKED)
        val beatmap = createBeatmap(beatmapSet.id, "DLNSTFO", 1)


        val standardUser = createUser("DLNSTFOOsu")
        createScore(standardUser, 1000, 1, beatmap.id, GameMode.OSU)

        val taikoUser = createUser("DLNSTFOTaiko")
        createScore(taikoUser, 1000, 1, beatmap.id, GameMode.TAIKO)

        val standardBuffer = ByteArrayOutputStream()
        requestRoute(otherMods.username, "", GameMode.OSU, "DLNSTFO", standardBuffer)

        val responseString = String(standardBuffer.toByteArray())

        assert(responseString.contains(standardUser.username))
        assertFalse(responseString.contains(taikoUser.username))
    }

    @Test
    fun testDoesLeaderboardNotShowOsuFromTaiko() {
        val otherMods = createUser("DLNSOFT")


        val beatmapSet = createBeatmapSet("Artist", "Song", BeatmapStatus.RANKED)
        val beatmap = createBeatmap(beatmapSet.id, "DLNSOFT", 1)


        val standardUser = createUser("DLNSOFTOsu")
        createScore(standardUser, 1000, 1, beatmap.id, GameMode.OSU)

        val taikoUser = createUser("DLNSOFTTaiko")
        createScore(taikoUser, 1000, 1, beatmap.id, GameMode.TAIKO)

        val standardBuffer = ByteArrayOutputStream()
        requestRoute(otherMods.username, "", GameMode.TAIKO, "DLNSOFT", standardBuffer)

        val responseString = String(standardBuffer.toByteArray())

        assert(responseString.contains(taikoUser.username))
        assertFalse(responseString.contains(standardUser.username))
    }
}