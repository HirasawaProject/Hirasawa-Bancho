package io.hirasawa.server.webserver.routingengine.httpcallable

import io.hirasawa.server.webserver.respondable.AssetRespondable
import io.hirasawa.server.webserver.respondable.HttpRespondable
import kotlin.reflect.KFunction

class AssetCallable (val assetLocation: String): HttpCallable {
    override fun call(): HttpRespondable {
        return AssetRespondable(assetLocation)
    }
}