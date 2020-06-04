package io.hirasawa.server.plugin

import io.hirasawa.server.logger.PluginLogger

abstract class HirasawaPlugin(protected val pluginDescriptor: PluginDescriptor): IHirasawaPlugin {
    protected var logger = PluginLogger(pluginDescriptor)
}