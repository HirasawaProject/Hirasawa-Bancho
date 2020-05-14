package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.io.OsuWriter
import java.io.ByteArrayOutputStream

abstract class BanchoPacket(val packetType: BanchoPacketType) {
    private val buffer = ByteArrayOutputStream()
    protected val writer = OsuWriter(buffer)

    /**
     * Generate and write the full packet to the provided writer
     *
     * @param writer The writer to write the packet to
     */
    fun write(output: OsuWriter) {
        val array = buffer.toByteArray() // This will be written by the extended class

        output.writeShort(packetType.id)
        output.writeByte(0)
        output.writeInt(array.size)
        output.writeBytes(array)
    }
}