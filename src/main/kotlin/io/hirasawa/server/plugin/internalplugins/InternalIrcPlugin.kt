package io.hirasawa.server.plugin.internalplugins

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.irc.servercommands.*
import io.hirasawa.server.irc.threads.IrcServerThread
import io.hirasawa.server.plugin.HirasawaPlugin
import io.hirasawa.server.plugin.PluginDescriptor
import io.hirasawa.server.routes.HomeRoute
import io.hirasawa.server.webserver.enums.CommonDomains
import io.hirasawa.server.webserver.enums.HttpMethod

class InternalIrcPlugin: HirasawaPlugin() {
    override fun onEnable() {
        Hirasawa.irc.registerServerCommand("JOIN", JoinCommand())
        Hirasawa.irc.registerServerCommand("LIST", ListCommand())
        Hirasawa.irc.registerServerCommand("PING", PingCommand())
        Hirasawa.irc.registerServerCommand("PART", PartCommand())
        Hirasawa.irc.registerServerCommand("PRIVMSG", PrivmsgCommand())
        Hirasawa.irc.registerServerCommand("QUIT", QuitCommand())
        Hirasawa.irc.start()
    }

    override fun onDisable() {
        // TODO add ability to remove web routes
    }

    companion object {
        val descriptor = PluginDescriptor("Hirasawa IRC", Hirasawa.version, "Hirasawa Contributors",
            "InternalIrcPlugin")
    }
}