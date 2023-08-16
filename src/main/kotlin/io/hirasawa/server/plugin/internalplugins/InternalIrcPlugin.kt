package io.hirasawa.server.plugin.internalplugins

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.commands.SetupIrcCommand
import io.hirasawa.server.irc.servercommands.*
import io.hirasawa.server.plugin.HirasawaPlugin
import io.hirasawa.server.plugin.PluginDescriptor

class InternalIrcPlugin(private val defaultPort: Int = Hirasawa.config.ircPort): HirasawaPlugin() {
    override fun onEnable() {
        Hirasawa.irc.registerServerCommand("JOIN", JoinCommand())
        Hirasawa.irc.registerServerCommand("LIST", ListCommand())
        Hirasawa.irc.registerServerCommand("PING", PingCommand())
        Hirasawa.irc.registerServerCommand("PART", PartCommand())
        Hirasawa.irc.registerServerCommand("PRIVMSG", PrivmsgCommand())
        Hirasawa.irc.registerServerCommand("QUIT", QuitCommand())

        registerCommand(SetupIrcCommand())
        Hirasawa.irc.start(defaultPort)
    }

    override fun onDisable() {
        // TODO add ability to remove web routes
    }

    companion object {
        val descriptor = PluginDescriptor("Hirasawa IRC", Hirasawa.version, "Hirasawa Contributors",
            "InternalIrcPlugin")
    }
}