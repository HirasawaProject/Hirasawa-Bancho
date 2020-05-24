package io.hirasawa.server.handlers

import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import java.io.DataOutputStream

class GetScoresErrorHeaderHandler(val beatmapStatus: BeatmapStatus, val haveOsz2: Boolean) {
    fun write(outputStream: DataOutputStream) {
        outputStream.writeBytes("${beatmapStatus.id}|${haveOsz2}")
    }
}