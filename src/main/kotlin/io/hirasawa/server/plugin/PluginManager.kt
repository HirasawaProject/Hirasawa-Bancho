package io.hirasawa.server.plugin

import com.google.gson.Gson
import io.hirasawa.server.Hirasawa
import io.hirasawa.server.logger.PluginLogger
import io.hirasawa.server.plugin.event.plugin.PluginLoadEvent
import io.hirasawa.server.plugin.event.plugin.PluginUnloadEvent
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.full.createInstance


class PluginManager {
    val loadedPlugins = HashMap<String, HirasawaPlugin>()

    fun loadPlugin(hirasawaPlugin: HirasawaPlugin, pluginDescriptor: PluginDescriptor) {
        hirasawaPlugin.pluginDescriptor = pluginDescriptor
        hirasawaPlugin.logger = PluginLogger(pluginDescriptor)

        PluginLoadEvent(pluginDescriptor).call().then {
            loadedPlugins[pluginDescriptor.name] = hirasawaPlugin
            hirasawaPlugin.onEnable()
        }
    }

    fun unloadPlugin(name: String): Boolean {
        if (name !in loadedPlugins.keys) {
            return false;
        }

        val plugin = loadedPlugins[name] ?: return false

        PluginUnloadEvent(plugin.pluginDescriptor).call().then {
            Hirasawa.eventHandler.removeEvents(plugin)
            Hirasawa.chatEngine.removeCommands(plugin)
            plugin.onDisable()
            loadedPlugins.remove(name)
        }

        return true
    }

    fun loadPluginsFromDirectory(directory: File, autocreate: Boolean) {
        if (!directory.exists()) {
            if (autocreate) {
                directory.mkdirs()
            } else {
                throw FileNotFoundException()
            }
        }

        for (file in directory.listFiles()) {
            if (file.name.endsWith(".jar")) {
                // Probably a plugin
                val jarFile = JarFile(file)

                val entry = jarFile.getJarEntry("plugin.json") ?:
                    throw FileNotFoundException("Plugin does not contain descriptor files")
                val reader = InputStreamReader(jarFile.getInputStream(entry))

                val descriptor = Gson().fromJson(reader, PluginDescriptor::class.java)

                val loader: ClassLoader = URLClassLoader.newInstance(
                    arrayOf(file.toURI().toURL()),
                    javaClass.classLoader
                )
                val kClass = Class.forName(descriptor.main, true, loader).kotlin

                val plugin = kClass.createInstance() as HirasawaPlugin

                loadPlugin(plugin, descriptor)
            }
        }

    }
}