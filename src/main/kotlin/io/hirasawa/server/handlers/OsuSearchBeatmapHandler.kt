package io.hirasawa.server.handlers

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.objects.BeatmapSet
import java.io.DataOutputStream
import java.time.Instant

class OsuSearchBeatmapHandler(private val beatmapSet: BeatmapSet) {
    private val separator = "|"
    fun write(outputStream: DataOutputStream) {
        outputStream.writeWithSeparator("${beatmapSet.id}.osz") // filename
        outputStream.writeWithSeparator(beatmapSet.artist)
        outputStream.writeWithSeparator(beatmapSet.title)
        outputStream.writeWithSeparator("Unknown") // TODO mapper
        outputStream.writeWithSeparator("1")
        outputStream.writeWithSeparator("10") // TODO rating
//        outputStream.writeWithSeparator(Instant.now().toString()) // TODO last update
        outputStream.writeWithSeparator("2020-07-01T13:42:33+00:00")
        outputStream.writeWithSeparator("${beatmapSet.id}")
        outputStream.writeWithSeparator("${beatmapSet.id}") // forum ID, we don't use this
        outputStream.writeWithSeparator("1") // TODO video
        outputStream.writeWithSeparator("") // storyboard, not used
        outputStream.writeWithSeparator("0") // filesize, not used
        outputStream.writeWithSeparator("") // filesize without video


        val difficultyNames = ArrayList<String>()
        for (beatmap in beatmapSet.difficulties) {
            difficultyNames.add("${beatmap.difficulty}@0")
        }

        outputStream.writeBytes(difficultyNames.joinToString(","))
        outputStream.writeBytes("\n")
    }

    private fun DataOutputStream.writeWithSeparator(value: String) {
        this.writeBytes("$value$separator")
    }
}


