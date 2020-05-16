package io.hirasawa.server.routes.test

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.webserver.Route
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import java.lang.Exception

class TestGetUserAuthRoute: Route {
    override fun handle(request: Request, response: Response) {
        try {
            val auth = Hirasawa.database.authenticate(request.get["username"]!!, request.get["password"]!!)
            response.writeText(auth.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
