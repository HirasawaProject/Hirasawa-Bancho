package io.hirasawa.server.update

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hirasawa.server.Hirasawa
import okhttp3.OkHttpClient
import java.lang.IllegalArgumentException

class UpdateChecker {
    var isUpdateRequired = false
    var latestRelease: Release? = null

    fun checkUpdate(): Boolean {
        val client = OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("https://api.github.com/repos/Hirasawa-Project/HirasawaServer/releases/latest")
            .build()

        val response = client.newCall(request).execute()

        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        val releases = gson.fromJson<Releases>(response.body?.string(), Releases::class.java)
        isUpdateRequired = currentVersion < releases.tagName.toFloat()
        latestRelease = releases

        return isUpdateRequired
    }
}