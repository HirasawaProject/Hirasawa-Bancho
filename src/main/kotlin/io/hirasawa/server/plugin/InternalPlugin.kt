package io.hirasawa.server.plugin

import io.hirasawa.server.commands.HelpCommand
import io.hirasawa.server.commands.TestCommand

class InternalPlugin: HirasawaPlugin() {
    override fun onEnable() {
        registerCommand(TestCommand())
        registerCommand(HelpCommand())
    }

    override fun onDisable() {

    }
}