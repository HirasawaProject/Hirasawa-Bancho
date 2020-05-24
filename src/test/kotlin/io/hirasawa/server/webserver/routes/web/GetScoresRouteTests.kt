package io.hirasawa.server.webserver.routes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.database.MemoryDatabase
import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import io.hirasawa.server.objects.Score
import io.hirasawa.server.permissions.PermissionGroup
import io.hirasawa.server.routes.web.OsuOsz2GetScoresRoute
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GetScoresRouteTests {
    val database = MemoryDatabase()
    @BeforeEach
    fun setupDb() {
        Hirasawa.database = database
    }

    private fun createUser(id: Int, username: String): User {
        return BanchoUser(id, username, 0, 0, ArrayList<PermissionGroup>(), GameMode.OSU, 0F,
            0F, UUID.randomUUID(), false)
    }

    private fun createScore(id: Int, user: User, score: Int, rank: Int, beatmapId: Int, gamemode: GameMode): Score {
        return Score(id, user, score, 100, 50, 100, 300, 0, 10,
            10, true, 0, 0, gamemode, rank, beatmapId)
    }

    private fun createBeatmap(id: Int, mapsetId: Int, hash: String, ranks: Int): Beatmap {
        return Beatmap(id, mapsetId, "HARD", hash, ranks, 0F)
    }

    private fun createBeatmapSet(id: Int, artist: String, title: String, status: BeatmapStatus): BeatmapSet {
        return BeatmapSet(id, artist, title, status)
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
        database.users.add(createUser(1, "CurrentStatus"))

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