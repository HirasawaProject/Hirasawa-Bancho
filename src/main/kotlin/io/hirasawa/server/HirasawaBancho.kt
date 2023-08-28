package io.hirasawa.server

import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.ChatChannelMetadata
import io.hirasawa.server.chat.GlobalChatChannel
import io.hirasawa.server.chat.channel.ConsoleChannel
import io.hirasawa.server.chat.command.ConsoleCommandSender
import io.hirasawa.server.chat.enums.ChatChannelVisibility
import io.hirasawa.server.objects.UserMap
import io.hirasawa.server.plugin.internalplugins.InternalBanchoPlugin
import io.hirasawa.server.plugin.internalplugins.InternalIrcPlugin
import io.hirasawa.server.threads.CacheInvalidationThread
import io.hirasawa.server.update.Release
import java.io.File
import java.util.concurrent.Executors

fun main() {
    println("Starting Hirasawa Bancho v${Hirasawa.version}")
    Hirasawa.initDatabase()
    // Register internal plugins, these are used to separate out our functionality so users can disable if needed
    Hirasawa.pluginManager.loadPlugin(InternalBanchoPlugin(), InternalBanchoPlugin.descriptor)
    Hirasawa.pluginManager.loadPlugin(InternalIrcPlugin(), InternalIrcPlugin.descriptor)
    val threadExecutor = Executors.newSingleThreadScheduledExecutor()
    threadExecutor.scheduleAtFixedRate(CacheInvalidationThread(), 0, 10, java.util.concurrent.TimeUnit.MINUTES)

    // Load user provided plugins
    Hirasawa.pluginManager.loadPluginsFromDirectory(File("plugins"), true)

    Hirasawa.webserver.start()

    if (Hirasawa.isUpdateRequired) {
        println("You are running an outdated version of Hirasawa, please update by going to the following link")
        println(Hirasawa.updateChecker.latestRelease?.getRelease(Release.AssetType.HIRASAWA_RELEASE)?.browserDownloadUrl)
    }

    // Hardcoded fake channel to get console responses
    val consoleChatChannel = ConsoleChannel()
    while (true) {
        Hirasawa.chatEngine.handleCommand(readlnOrNull()?.split(" ")!!, ConsoleCommandSender(), consoleChatChannel)
    }
}