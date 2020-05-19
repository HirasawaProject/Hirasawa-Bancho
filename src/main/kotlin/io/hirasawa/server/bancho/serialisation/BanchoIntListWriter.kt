package io.hirasawa.server.bancho.serialisation

import io.hirasawa.server.bancho.io.OsuWriter

class BanchoIntListWriter(private val list: List<Int>): SerialisedBanchoObject {
    override fun write(writer: OsuWriter) {
        writer.writeShort(list.size.toShort())
        for (int in list) {
            writer.writeInt(int)
        }
    }

}