package io.hirasawa.server.routes.test

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.config.HirasawaConfig
import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class TestGetBanchoUsersRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.writeText("Online Bancho users:")
        response.writeText("<ol>")
        for (user in Hirasawa.banchoUsers) {
            response.writeText("<li>${user.username}</li>")
        }
        response.writeText("</ol>")
    }
}