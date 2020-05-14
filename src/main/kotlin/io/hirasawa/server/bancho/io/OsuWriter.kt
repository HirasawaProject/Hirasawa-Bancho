package io.hirasawa.server.bancho.io

import java.io.DataOutputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Writer for osu! specific files and networking
 *
 * Based off the work from Markus Jarderot (https://stackoverflow.com/questions/28788616/parse-the-osu-binary-database-in-java)
 */
class OsuWriter(source: OutputStream?) {
    private val writer: DataOutputStream

    constructor(filename: String?) : this(FileOutputStream(filename)) {}

    @Throws(IOException::class)
    fun writeByte(data: Byte) {
        // 1 byte
        writer.writeByte(data.toInt())
    }

    @Throws(IOException::class)
    fun writeShort(data: Short) {
        // 2 bytes, little endian
        val bytes =
            ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(data).array()
        writer.write(bytes)
    }

    @Throws(IOException::class)
    fun writeInt(data: Int) {
        // 4 bytes, little endian
        val bytes =
            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(data).array()
        writer.write(bytes)
    }

    @Throws(IOException::class)
    fun writeLong(data: Long) {
        // 8 bytes, little endian
        val bytes =
            ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(data).array()
        writer.write(bytes)
    }

    @Throws(IOException::class)
    fun writeULEB128(data: Int) {
        var data = data
        var bytesWritten = 0
        do {
            var groupValue = (data and 0x7F).toByte()
            data = data ushr 7
            if (data != 0) {
                groupValue = groupValue or 0x80
            }
            writer.writeByte(data)
            bytesWritten++
        } while (data != 0)
    }

    @Throws(IOException::class)
    fun writeFloat(data: Float) {
        // 4 bytes, little endian
        val bytes =
            ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(data).array()
        writer.write(bytes)
    }

    @Throws(IOException::class)
    fun writeDouble(data: Double) {
        // 8 bytes little endian
        val bytes =
            ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(data).array()
        writer.write(bytes)
    }

    @Throws(IOException::class)
    fun writeBoolean(data: Boolean) {
        // 1 byte, zero = false, non-zero = true
        writer.writeBoolean(data)
    }

    @Throws(IOException::class)
    fun writeString(data: String) {
        if (data.isEmpty()) {
            // If empty set kind to 0 for minimal data
            writer.writeByte(0)
            return
        }

        // Set kind to 11 if contains data
        writer.writeByte(11)
        writeULEB128(data.length)
        writer.writeUTF(data)
    }

    @Throws(IOException::class)
    fun writeBytes(array: ByteArray) {
        for (value in array) {
            writer.writeByte(value.toInt())
        }
    }

    @Throws(IOException::class)
    fun close() {
        writer.close()
    }

    init {
        writer = DataOutputStream(source)
    }
}