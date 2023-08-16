package io.hirasawa.server.update

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hirasawa.server.Hirasawa
import okhttp3.OkHttpClient
import java.lang.IllegalArgumentException
import java.lang.NullPointerException

class UpdateChecker {
    var isUpdateRequired = false
    var latestRelease: Release? = null

    fun checkUpdate(): Boolean {
        if (Hirasawa.version.buildMetadata == "noupdate") {
            println("Your build has been configured to ignore updates")
            return false
        }
        val client = OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url("https://api.github.com/repos/HirasawaProject/Hirasawa-Server/releases/latest")
            .build()

        val response = client.newCall(request).execute()

        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        val release = gson.fromJson(response.body?.string(), Release::class.java)
        if (release != null) {
            try {
                isUpdateRequired = Hirasawa.version < release.semver
                latestRelease = release
            } catch (exception: IllegalArgumentException) {
                printError()
            }
        } else {
            printError()
        }

        return isUpdateRequired
    }

    private fun printError() {
        println("Latest version of Hirasawa can't be parsed, either your version is too old or the latest " +
                "release is broken")
        println("You may need to update manually if you're on an old version or open a ticket for this issue")
    }

}