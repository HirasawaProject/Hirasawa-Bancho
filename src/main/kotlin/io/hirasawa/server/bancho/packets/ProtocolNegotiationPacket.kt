package io.hirasawa.server.bancho.packets

class ProtocolNegotiationPacket(protocolVersion: Int): BanchoPacket(BanchoPacketType.BANCHO_PROTOCOL_NEGOTIATION) {
    init {
        writer.writeInt(protocolVersion)
    }
}