package io.hirasawa.server.bancho.io

import java.io.DataInputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

/**
 * Reader for osu! specific files and networking
 *
 * @author Markus Jarderot (https://stackoverflow.com/questions/28788616/parse-the-osu-binary-database-in-java)
 */
class OsuReader(source: InputStream?) {
    private val reader: DataInputStream

    constructor(filename: String?) : this(FileInputStream(filename)) {}

    @Throws(IOException::class)
    fun readByte(): Byte {
        // 1 byte
        return reader.readByte()
    }

    @Throws(IOException::class)
    fun readShort(): Short {
        // 2 bytes, little endian
        val bytes = ByteArray(2)
        reader.readFully(bytes)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return bb.short
    }

    @Throws(IOException::class)
    fun readInt(): Int {
        // 4 bytes, little endian
        val bytes = ByteArray(4)
        reader.readFully(bytes)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return bb.int
    }

    @Throws(IOException::class)
    fun readLong(): Long {
        // 8 bytes, little endian
        val bytes = ByteArray(8)
        reader.readFully(bytes)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return bb.long
    }

    @Throws(IOException::class)
    fun readULEB128(): Int {
        // variable bytes, little endian
        // MSB says if there will be more bytes. If cleared,
        // that byte is the last.
        var value = 0
        var shift = 0
        while (shift < 32) {
            val b = reader.readByte()
            value = value or (b.toInt() and 0x7F shl shift)
            if (b >= 0) return value // MSB is zero. End of value.
            shift += 7
        }
        throw IOException("ULEB128 too large")
    }

    @Throws(IOException::class)
    fun readSingle(): Float {
        // 4 bytes, little endian
        val bytes = ByteArray(4)
        reader.readFully(bytes)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return bb.float
    }

    @Throws(IOException::class)
    fun readDouble(): Double {
        // 8 bytes little endian
        val bytes = ByteArray(8)
        reader.readFully(bytes)
        val bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return bb.double
    }

    @Throws(IOException::class)
    fun readBoolean(): Boolean {
        // 1 byte, zero = false, non-zero = true
        return reader.readBoolean()
    }

    @Throws(IOException::class)
    fun readString(): String {
        // Kind describes how to handle the length
        // 0: the string is empty
        // 11: the the string is not empty
        val kind = reader.readByte()
        if (kind.toInt() == 0) {
            return ""
        }
        if (kind.toInt() != 11) throw IOException("Invalid string kind")
        val length = readULEB128()
        if (length == 0) {
            return ""
        }
        val byteArray = ByteArray(length)
        reader.readFully(byteArray)
        return String(byteArray, StandardCharsets.UTF_8)
    }

    @Throws(IOException::class)
    fun skipBytes(n: Int) {
        reader.skipBytes(n)
    }

    init {
        reader = DataInputStream(source)
    }
}