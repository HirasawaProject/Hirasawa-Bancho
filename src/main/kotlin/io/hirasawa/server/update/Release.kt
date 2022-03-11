package io.hirasawa.server.update

import java.util.*
import kotlin.collections.ArrayList

data class Release(val url: String, val assetsUrl: String, val tagName: String, val assets: ArrayList<Release>,
                    val name: String, val createdAt: Date, val browserDownloadUrl: String) {
        val semver: SemVer by lazy {
            SemVer.parse(name)
        }
}
