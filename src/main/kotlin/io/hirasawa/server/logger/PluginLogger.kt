package io.hirasawa.server.logger

import io.hirasawa.server.plugin.PluginDescriptor
import java.io.File

class PluginLogger(private val pluginDescriptor: PluginDescriptor):
        FileLogger(File("/logs/${pluginDescriptor.main}.txt")) {
    override fun log(message: Any) {
        super.log("[${pluginDescriptor.name}] $message")
    }
}