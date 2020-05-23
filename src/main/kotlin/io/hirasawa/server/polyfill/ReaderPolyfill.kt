package io.hirasawa.server.polyfill

import java.io.ByteArrayInputStream
import java.io.InputStream

fun InputStream.readNBytes(toInt: Int): ByteArray {
    val array = ByteArray(toInt)

    for (x in 0 until toInt) {
        array[x] = this.read().toByte()
    }

    return array
}

fun ByteArrayInputStream.readAllBytes(): ByteArray {
    return this.readNBytes(this.available())
}