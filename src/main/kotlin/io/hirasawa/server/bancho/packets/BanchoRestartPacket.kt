package io.hirasawa.server.bancho.packets

class BanchoRestartPacket(val waitTime: Int): BanchoPacket(BanchoPacketType.BANCHO_RESTART) {
    init {
        writer.writeInt(waitTime)
    }
}