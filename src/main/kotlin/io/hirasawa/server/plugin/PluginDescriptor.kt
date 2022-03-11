package io.hirasawa.server.plugin

import io.hirasawa.server.update.SemVer

data class PluginDescriptor(val name: String, val version: SemVer, val author: String, val main: String)