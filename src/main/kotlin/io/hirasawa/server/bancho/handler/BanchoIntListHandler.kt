package io.hirasawa.server.bancho.handler

import io.hirasawa.server.bancho.io.OsuReader
import java.io.ByteArrayInputStream
import java.io.DataInputStream

class BanchoIntListHandler(reader: OsuReader) {
    val intList = ArrayList<Int>()

    init {
        val size = reader.readShort()
        for (i in 0 until size) {
            intList.add(reader.readInt())
        }
    }
}