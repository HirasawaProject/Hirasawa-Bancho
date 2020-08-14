package io.hirasawa.server.bancho.handler

import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.objects.ReplayFrame
import java.io.ByteArrayInputStream
import java.io.DataInputStream

class ReplayFrameHandler(reader: OsuReader) {
    val replayFrame: ReplayFrame

    init {
        val buttonState = reader.readByte()
        reader.skipBytes(1)
        val mouseX = reader.readSingle()
        val mouseY = reader.readSingle()
        val timeframe = reader.readInt()
        replayFrame = ReplayFrame(buttonState, mouseX, mouseY, timeframe)
    }
}