package io.hirasawa.server.webserver.enums

import io.hirasawa.server.Hirasawa

enum class CommonDomains(val domain: String) {
    OSU_WEB("osu.${Hirasawa.config.domain}"),
    OSU_AVATAR("a.${Hirasawa.config.domain}"),
    OSU_BEATMAPS("b.${Hirasawa.config.domain}"),
    OSU_BANCHO("c.${Hirasawa.config.domain}"),
    HIRASAWA_WEB(Hirasawa.config.domain),
    HIRASAWA_IRC("irc.${Hirasawa.config.domain}");

    override fun toString(): String {
        return this.domain
    }
}