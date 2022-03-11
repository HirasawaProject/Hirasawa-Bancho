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
            lazySemver = SemVer.parse(tagName)
        }
        return lazySemver
    }

    fun getRelease(releaseType: AssetType): Asset? {
        for (asset in assets) {
            if (releaseType == releaseType) {
                return asset
            }
        }
        return null
    }

    data class Asset(val url: String, val browserDownloadUrl: String, val name: String) {
        val releaseType: AssetType get() {
            return if (name.endsWith("-all.jar")) {
                AssetType.HIRASAWA_RELEASE
            } else {
                AssetType.UNKNOWN
            }
        }
    }

    enum class AssetType {
        HIRASAWA_RELEASE,
        UNKNOWN
    }
}
