package io.hirasawa.server.plugin

import io.hirasawa.server.logger.PluginLogger

abstract class HirasawaPlugin: IHirasawaPlugin {
    lateinit var pluginDescriptor: PluginDescriptor
    lateinit var logger: PluginLogger
}