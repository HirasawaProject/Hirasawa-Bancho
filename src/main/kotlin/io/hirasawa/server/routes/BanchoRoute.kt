package io.hirasawa.server.routes

import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.PacketHandler
import io.hirasawa.server.bancho.packethandler.SendIrcMessagePacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelAvailablePacket
import io.hirasawa.server.bancho.packets.LoginReplyPacket
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.enums.ContentType
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.routes.errors.RouteForbidden
import java.io.ByteArrayInputStream
import java.util.*

class BanchoRoute: Route {
    override fun handle(request: Request, response: Response) {
        if (request.headers["User-Agent"] != "osu!") {
            // Only osu! should be able to contact Bancho
            // Tell RouteForbidden to handle this request for us
            RouteForbidden().handle(request, response)
            return
        }

        response.headers[HttpHeader.CONTENT_TYPE.toString()] = ContentType.APPLICATION_OCTET_STREAM.toString()
        response.headers["cho-version"] = "19"

        val osuWriter = OsuWriter(response.outputStream)
        val osuReader = OsuReader(request.inputStream)

        if ("osu-token" !in request.headers) {
            // No osu! token, we need to read the login information from the client
            // This is oddly not a standard packet so we need to handle it as a one-off
            // TODO handle login
            // We'll generate their token now, we'll store it when we actually do auth, not sure why it's not osu-token
            // but we'll roll with it
            response.headers["cho-token"] = UUID.randomUUID().toString()
            // We'll just say they're user ID 1 right now
            LoginReplyPacket(1).write(osuWriter)
            ChannelAvailablePacket("#osu").write(osuWriter)
            return
        }

        val user = BanchoUser(1, "Connor") // TODO this data up from somewhere later

        // TODO move this somewhere else, this will be used to extend new packets
        val packetRouter = HashMap<BanchoPacketType, PacketHandler>()
        packetRouter[BanchoPacketType.OSU_SEND_IRC_MESSAGE] = SendIrcMessagePacket()

        while (request.inputStream.available() > 1) {
            val id = osuReader.readShort()
            osuReader.skipBytes(1) // unused byte
            val payloadLength = osuReader.readInt()
            val payload = request.inputStream.readNBytes(payloadLength)

            val packetReader = OsuReader(ByteArrayInputStream(payload))
            val banchoPacketType = BanchoPacketType.fromId(id)

            if (banchoPacketType == BanchoPacketType.UNKNOWN) continue

            packetRouter[banchoPacketType]?.handle(packetReader, osuWriter, user)
        }


        for (packet in user.packetCache) {
            packet.write(osuWriter)
        }
        user.packetCache.clear()

    }

}