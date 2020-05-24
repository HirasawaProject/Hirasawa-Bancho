package io.hirasawa.server.handlers

import io.hirasawa.server.enums.BeatmapStatus
import io.hirasawa.server.objects.Beatmap
import io.hirasawa.server.objects.BeatmapSet
import java.io.DataOutputStream

class GetScoresHeaderHandler(val haveOsz2: Boolean, val beatmap: Beatmap, val beatmapSet: BeatmapSet) {
    fun write(outputStream: DataOutputStream) {
        outputStream.writeBytes("${beatmapSet.status.id}|${haveOsz2}|${beatmap.id}|${beatmapSet.id}|${beatmap.ranks}\n")
        outputStream.writeBytes("0\n")
        outputStream.writeBytes("[bold:0,size:20] ${beatmapSet.artist}|${beatmapSet.title}\n")
        outputStream.writeBytes("${beatmap.offset}\n")
    }
}