package io.hirasawa.server.update

import java.util.*
import kotlin.collections.ArrayList

data class Releases(val url: String, val assetsUrl: String, val tagName: String, val assets: ArrayList<Release>) {
    data class Release(val url: String, val name: String, val createdAt: Date, val browserDownloadUrl: String)
}
