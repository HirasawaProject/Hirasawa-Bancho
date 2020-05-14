package io.hirasawa.server.routes

import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.ChannelAvailablePacket
import io.hirasawa.server.bancho.packets.LoginReplyPacket
import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.enums.ContentType
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.routes.errors.RouteForbidden
import java.util.*

class BanchoRoute: Route {
    override fun handle(request: Request, response: Response) {
        if (response.headers["User-Agent"] != "osu!") {
            // Only osu! should be able to contact Bancho
            // Tell RouteForbidden to handle this request for us
            RouteForbidden().handle(request, response)
            return
        }

        response.headers[HttpHeader.CONTENT_TYPE.toString()] = ContentType.APPLICATION_OCTET_STREAM.toString()
        request.headers["cho-version"] = "19"

        val osuWriter = OsuWriter(response.outputStream)

        if ("osu-token" !in request.headers) {
            // No osu! token, we need to read the login information from the client
            // This is oddly not a standard packet so we need to handle it as a one-off
            // TODO handle login
            // We'll generate their token now, we'll store it when we actually do auth, not sure why it's not osu-token
            // but we'll roll with it
            request.headers["cho-token"] = UUID.randomUUID().toString()
            // We'll just say they're user ID 1 right now
            LoginReplyPacket(1).write(osuWriter)
            ChannelAvailablePacket("#osu").write(osuWriter)
        }

    }

}