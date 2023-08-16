package io.hirasawa.server.osuapi

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import io.hirasawa.server.bancho.enums.Mode
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.time.Instant


class OsuApi(private val key: String) {
    private val baseUrl = "https://osu.ppy.sh/api"
    private val client = OkHttpClient()
    private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    fun getBeatmaps(since: Instant? = null,
                    mapsetId: Int? = null,
                    beatmapId: Int? = null,
                    userId: Int? = null,
                    mode: Mode? = null,
                    allowConverted: Boolean? = null,
                    beatmapHash: String? = null,
                    limit: Int = 500,
                    mods: Int = 0): Array<OsuApiBeatmap> {
        if (key.isEmpty()) throw NoApiKeyException()

        val httpBuilder = "$baseUrl/get_beatmaps".toHttpUrl().newBuilder()
        httpBuilder.addQueryParameterIfNotNull("since", since)
        httpBuilder.addQueryParameterIfNotNull("s", mapsetId)
        httpBuilder.addQueryParameterIfNotNull("b", beatmapId)
        httpBuilder.addQueryParameterIfNotNull("u", userId)
        httpBuilder.addQueryParameterIfNotNull("m", mode?.ordinal)
        httpBuilder.addQueryParameterIfNotNull("a", allowConverted)
        httpBuilder.addQueryParameterIfNotNull("h", beatmapHash)
        httpBuilder.addQueryParameterIfNotNull("limit", limit)
        httpBuilder.addQueryParameterIfNotNull("mods", mods)
        httpBuilder.addQueryParameterIfNotNull("k", key)

        val request = okhttp3.Request.Builder()
            .url(httpBuilder.build())
            .build()

        val response = client.newCall(request).execute()

        return gson.fromJson(response.body!!.string(), Array<OsuApiBeatmap>::class.java)
    }

    fun getUser(userId: Int? = null,
                userName: String? = null,
                mode: Mode? = null,
                eventDays: Int = 1): Array<OsuApiUser> {
        if (key.isEmpty()) throw NoApiKeyException()

        val httpBuilder = "$baseUrl/get_user".toHttpUrl().newBuilder()

        httpBuilder.addQueryParameterIfNotNull("u", userId)
        httpBuilder.addQueryParameterIfNotNull("u", userName)
        httpBuilder.addQueryParameterIfNotNull("m", mode?.ordinal)
        httpBuilder.addQueryParameterIfNotNull("event_days", eventDays)
        httpBuilder.addQueryParameterIfNotNull("k", key)

        val request = okhttp3.Request.Builder()
            .url(httpBuilder.build())
            .build()

        val response = client.newCall(request).execute()

        return gson.fromJson(response.body!!.string(), Array<OsuApiUser>::class.java)
    }

    fun getScores(beatmapId: Int,
                  userId: Int? = null,
                  userName: String? = null,
                  mode: Mode? = null,
                  mods: Int? = null,
                  limit: Int = 50): Array<OsuApiScore> {
        if (key.isEmpty()) throw NoApiKeyException()

        val httpBuilder = "$baseUrl/get_scores".toHttpUrl().newBuilder()

        httpBuilder.addQueryParameterIfNotNull("b", beatmapId)
        httpBuilder.addQueryParameterIfNotNull("u", userId)
        httpBuilder.addQueryParameterIfNotNull("u", userName)
        httpBuilder.addQueryParameterIfNotNull("m", mode?.ordinal)
        httpBuilder.addQueryParameterIfNotNull("mods", mods)
        httpBuilder.addQueryParameterIfNotNull("limit", limit)
        httpBuilder.addQueryParameterIfNotNull("k", key)

        val request = okhttp3.Request.Builder()
            .url(httpBuilder.build())
            .build()

        val response = client.newCall(request).execute()

        return gson.fromJson(response.body!!.string(), Array<OsuApiScore>::class.java)
    }

    fun getUserBest(userId: Int? = null,
                    userName: String? = null,
                    limit: Int = 10): Array<OsuApiScore> {
        if (key.isEmpty()) throw NoApiKeyException()

        val httpBuilder = "$baseUrl/get_user_best".toHttpUrl().newBuilder()

        httpBuilder.addQueryParameterIfNotNull("u", userId)
        httpBuilder.addQueryParameterIfNotNull("u", userName)
        httpBuilder.addQueryParameterIfNotNull("limit", limit)
        httpBuilder.addQueryParameterIfNotNull("k", key)

        val request = okhttp3.Request.Builder()
            .url(httpBuilder.build())
            .build()

        val response = client.newCall(request).execute()

        return gson.fromJson(response.body!!.string(), Array<OsuApiScore>::class.java)
    }

    fun getMatch(matchId: Int): OsuApiMatch {
        if (key.isEmpty()) throw NoApiKeyException()

        val httpBuilder = "$baseUrl/get_match".toHttpUrl().newBuilder()

        httpBuilder.addQueryParameterIfNotNull("mp", matchId)
        httpBuilder.addQueryParameterIfNotNull("k", key)

        val request = okhttp3.Request.Builder()
            .url(httpBuilder.build())
            .build()

        val response = client.newCall(request).execute()

        return gson.fromJson(response.body!!.string(), OsuApiMatch::class.java)
    }

    private fun HttpUrl.Builder.addQueryParameterIfNotNull(key: String, value: Any?) {
        this.addQueryParameter(key, value?.toString() ?: return)
    }
}