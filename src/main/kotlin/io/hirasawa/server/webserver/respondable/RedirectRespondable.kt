package io.hirasawa.server.webserver.respondable

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class RedirectRespondable(private val redirectUrl: String): HttpRespondable() {
    override fun respond(request: Request, response: Response) {
        response.redirect(redirectUrl)
    }
}