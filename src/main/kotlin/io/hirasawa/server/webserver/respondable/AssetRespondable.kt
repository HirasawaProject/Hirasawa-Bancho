package io.hirasawa.server.webserver.respondable

import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response

class AssetRespondable(private val assetPath: String): HttpRespondable {
    override fun respond(request: Request, response: Response) {

    }
}