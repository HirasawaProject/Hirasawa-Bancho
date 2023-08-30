package io.hirasawa.server.update

import java.util.*
import kotlin.collections.ArrayList

data class Release(val url: String, val assetsUrl: String, val tagName: String, val assets: ArrayList<Asset>,
                    val name: String, val createdAt: Date) {
    // We're not using by lazy here since it seems to break when using GSON
    // Probably due to the library being Java-based rather than Kotlin
    // This is annoying but we'll have to deal with it
    private lateinit var lazySemver: SemVer
    val semver: SemVer get() {
        if (!this::lazySemver.isInitialized) {
            lazySemver = SemVer.parse(tagName.removePrefix("v"))
        }
        return lazySemver
    }

    fun getRelease(releaseType: AssetType): Asset? {
        for (asset in assets) {
            if (asset.releaseType == releaseType) {
                return asset
            }
        }
        return null
    }

    data class Asset(val url: String, val browserDownloadUrl: String, val name: String) {
        val releaseType: AssetType get() {
            val segments = name.split("-")
            return when {
                segments.size < 2 -> AssetType.UNKNOWN
                segments.size == 2 -> AssetType.HIRASAWA_RELEASE
                segments[1] == "javadoc" -> AssetType.JAVADOC
                segments[1] == "sources" -> AssetType.SOURCES
                segments[1] == "api" -> AssetType.API
                else -> AssetType.UNKNOWN
            }
        }
    }

    enum class AssetType {
        HIRASAWA_RELEASE,
        JAVADOC,
        SOURCES,
        API,
        UNKNOWN
    }
}
