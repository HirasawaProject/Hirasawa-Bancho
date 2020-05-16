package io.hirasawa.server.bancho.packets

class BanchoRestart(val waitTime: Int): BanchoPacket(BanchoPacketType.BANCHO_RESTART) {
    init {
        writer.writeInt(waitTime)
    }
}