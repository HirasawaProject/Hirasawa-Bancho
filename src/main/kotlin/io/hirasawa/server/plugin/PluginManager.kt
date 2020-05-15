package io.hirasawa.server.plugin

import com.google.gson.Gson
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.instrument.IllegalClassFormatException
import java.lang.reflect.Constructor
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.full.createInstance


class PluginManager {
    val loadedPlugins = HashMap<String, HirasawaPlugin>()

    fun loadPlugin(hirasawaPlugin: HirasawaPlugin, pluginDescriptor: PluginDescriptor) {
        loadedPlugins[pluginDescriptor.name] = hirasawaPlugin
        hirasawaPlugin.onEnable()
    }

    fun unloadPlugin(name: String): Boolean {
        if (name !in loadedPlugins.keys) {
            return false;
        }

        val plugin = loadedPlugins[name]
        plugin?.onDisable()
        loadedPlugins.remove(name)

        return true
    }

    fun loadPluginsFromDirectory(directory: File) {
        if (!directory.isDirectory || !directory.exists()) throw FileNotFoundException()

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