package io.hirasawa.server.routes.test

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import java.lang.Exception

class TestGetUserRoute: Route {
    override fun handle(request: Request, response: Response) {
        try {
            val user = Hirasawa.database.getUser(request.get["username"]!!)
            response.writeText("User(id=${user.id}, username=${user.username})")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
