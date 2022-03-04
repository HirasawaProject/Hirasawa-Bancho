package io.hirasawa.server.plugin.internalplugins

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.plugin.HirasawaPlugin
import io.hirasawa.server.plugin.PluginDescriptor
import io.hirasawa.server.routes.BeatmapDownloadRoute
import io.hirasawa.server.routes.BeatmapRoute
import io.hirasawa.server.routes.web.OsuOsz2GetScoresRoute
import io.hirasawa.server.routes.web.OsuSearchRoute
import io.hirasawa.server.routes.web.OsuSubmitModular
import io.hirasawa.server.webserver.enums.CommonDomains
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route

class InternalGameApiPlugin: HirasawaPlugin() {
    override fun onEnable() {
        registerWebRoutes()
    }

    override fun onDisable() {
        // TODO allow ability to remove web routes
    }

    private fun registerWebRoutes() {
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB,"/web/osu-osz2-getscores.php", HttpMethod.GET, OsuOsz2GetScoresRoute())
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/web/osu-submit-modular.php", HttpMethod.POST, OsuSubmitModular())
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/web/osu-submit-modular-selector.php", HttpMethod.POST, OsuSubmitModular())
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/b/{beatmap}", HttpMethod.GET, BeatmapRoute())
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/web/osu-search.php", HttpMethod.GET, OsuSearchRoute())
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/d/{beatmap}", HttpMethod.GET, BeatmapDownloadRoute())
        Hirasawa.webserver.addRoute(CommonDomains.OSU_WEB, "/", HttpMethod.GET, object: Route {
            override fun handle(request: Request, response: Response) {
                response.redirect("https://${Hirasawa.config.domain}")
            }
        })
    }

    companion object {
        val descriptor = PluginDescriptor("Hirasawa Game API", Hirasawa.version, "Hirasawa Contributors",
            "InternalGameApiPlugin")
    }
}