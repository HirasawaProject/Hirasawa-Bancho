package io.hirasawa.server.commands

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.chat.command.ChatCommand
import io.hirasawa.server.bancho.chat.command.CommandContext
import io.hirasawa.server.plugin.InternalPlugin
import io.hirasawa.server.plugin.PluginDescriptor
import java.io.File

class ReloadCommand: ChatCommand("reload", "Reloads the loaded plugins", "hirasawa.command.reload") {
    override fun onCommand(context: CommandContext, command: String, args: List<String>): Boolean {
        context.respond("WARNING! This command could cause memory leaks and other weird behaviour, don't rely on " +
                "this on production servers")

        val pluginNames = Hirasawa.pluginManager.loadedPlugins.keys.toMutableList()
        for (pluginName in pluginNames) {
            Hirasawa.pluginManager.unloadPlugin(pluginName)
        }

        Hirasawa.pluginManager.loadPluginsFromDirectory(File("plugins"), true)
        Hirasawa.pluginManager.loadPlugin(InternalPlugin(), PluginDescriptor("Internal Plugin", Hirasawa.version,
            "Hirasawa", "")
        )
        return true
    }

}