package io.hirasawa.server.bancho.packets

class TitleUpdatePacket(image: String, url:String): BanchoPacket(BanchoPacketType.BANCHO_TITLE_UPDATE) {
    init {
        writer.writeString("$image|$url")
    }
}