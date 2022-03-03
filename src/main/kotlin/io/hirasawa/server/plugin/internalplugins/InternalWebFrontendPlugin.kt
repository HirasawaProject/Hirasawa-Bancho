package io.hirasawa.server.plugin.internalplugins

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.plugin.HirasawaPlugin
import io.hirasawa.server.plugin.PluginDescriptor
import io.hirasawa.server.routes.HomeRoute
import io.hirasawa.server.webserver.enums.CommonDomains
import io.hirasawa.server.webserver.enums.HttpMethod

class InternalWebFrontendPlugin: HirasawaPlugin() {
    override fun onEnable() {
        Hirasawa.webserver.addRoute(CommonDomains.HIRASAWA_WEB, "/", HttpMethod.GET, HomeRoute())
    }

    override fun onDisable() {
        // TODO add ability to remove web routes
    }

    companion object {
        val descriptor = PluginDescriptor("Hirasawa Web Frontend", Hirasawa.version, "Hirasawa Contributors",
            "InternalWebFrontendPlugin")
    }
}