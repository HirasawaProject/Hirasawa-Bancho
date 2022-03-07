package io.hirasawa.server

import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.chat.command.ConsoleCommandSender
import io.hirasawa.server.plugin.internalplugins.InternalBanchoPlugin
import io.hirasawa.server.plugin.internalplugins.InternalGameApiPlugin
import io.hirasawa.server.plugin.internalplugins.InternalIrcPlugin
import io.hirasawa.server.plugin.internalplugins.InternalWebFrontendPlugin
import io.hirasawa.server.webserver.enums.CommonDomains
import java.io.File

fun main() {
    println("Starting Hirasawa v${Hirasawa.version}")
    Hirasawa.initDatabase()
    // Register internal plugins, these are used to separate out our functionality so users can disable if needed
    Hirasawa.pluginManager.loadPlugin(InternalBanchoPlugin(), InternalBanchoPlugin.descriptor)
    Hirasawa.pluginManager.loadPlugin(InternalWebFrontendPlugin(), InternalWebFrontendPlugin.descriptor)
    Hirasawa.pluginManager.loadPlugin(InternalGameApiPlugin(), InternalGameApiPlugin.descriptor)
    Hirasawa.pluginManager.loadPlugin(InternalIrcPlugin(), InternalIrcPlugin.descriptor)

    // Load user provided plugins
    Hirasawa.pluginManager.loadPluginsFromDirectory(File("plugins"), true)

    Hirasawa.webserver.start()

    if (Hirasawa.isUpdateRequired) {
        println("You are running an outdated version of Hirasawa, please update by going to the following link")
        println(Hirasawa.updateChecker.latestRelease?.assets?.first()?.browserDownloadUrl)
    }

    // Hardcoded fake channel to get console responses
    val consoleChatChannel = ChatChannel("!CONSOLE", "", false)
    while (true) {
        Hirasawa.chatEngine.handleCommand(readLine()?.split(" ")!!, ConsoleCommandSender(), consoleChatChannel)
    }
}