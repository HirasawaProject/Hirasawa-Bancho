package io.hirasawa.server.plugin.event.plugin

import io.hirasawa.server.plugin.PluginDescriptor
import io.hirasawa.server.plugin.event.HirasawaEvent

class PluginLoadEvent(val pluginDescriptor: PluginDescriptor): HirasawaEvent<PluginLoadEvent>