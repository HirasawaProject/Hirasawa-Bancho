package io.hirasawa.server.objects

import io.hirasawa.server.enums.BeatmapStatus

data class BeatmapSet(val id: Int, val artist: String, val title: String, val status: BeatmapStatus)