package io.hirasawa.server.plugin.internalplugins

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.controllers.BeatmapController
import io.hirasawa.server.controllers.ScoreController
import io.hirasawa.server.plugin.HirasawaPlugin
import io.hirasawa.server.plugin.PluginDescriptor
import io.hirasawa.server.webserver.enums.CommonDomains
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.respondable.RedirectRespondable

class InternalGameApiPlugin: HirasawaPlugin() {
    override fun onEnable() {
        registerWebRoutes()
    }

    override fun onDisable() {
        // TODO allow ability to remove web routes
    }

    private fun registerWebRoutes() {
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB,"/web/osu-osz2-getscores.php", HttpMethod.GET, ScoreController::get)
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/web/osu-submit-modular.php", HttpMethod.POST, ScoreController::submit)
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/web/osu-submit-modular-selector.php", HttpMethod.POST, ScoreController::submit)
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/b/{beatmap}", HttpMethod.GET, BeatmapController::index)
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/web/osu-search.php", HttpMethod.GET, BeatmapController::search)
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/d/{beatmap}", HttpMethod.GET, BeatmapController::download)
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/", HttpMethod.GET) { _, _ ->
            RedirectRespondable("https://${Hirasawa.config.domain}")
        }
    }

    companion object {
        val descriptor = PluginDescriptor("Hirasawa Game API", Hirasawa.version, "Hirasawa Contributors",
            "InternalGameApiPlugin")
    }
}