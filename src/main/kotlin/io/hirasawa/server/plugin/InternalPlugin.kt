package io.hirasawa.server.plugin

import io.hirasawa.server.commands.*

class InternalPlugin: HirasawaPlugin() {
    override fun onEnable() {
        registerCommand(TestCommand())
        registerCommand(HelpCommand())
        registerCommand(ReloadCommand())
        registerCommand(RankCommand())
        registerCommand(ReportCommand())
    }

    override fun onDisable() {

    }
}