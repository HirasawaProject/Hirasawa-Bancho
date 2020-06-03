package io.hirasawa.server

import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.chat.command.ConsoleCommandSender
import io.hirasawa.server.bancho.packethandler.*
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.threads.UserTimeoutThread
import io.hirasawa.server.commands.TestCommand
import io.hirasawa.server.database.MysqlDatabase
import io.hirasawa.server.routes.BanchoRoute
import io.hirasawa.server.routes.BeatmapRoute
import io.hirasawa.server.routes.web.OsuOsz2GetScoresRoute
import io.hirasawa.server.webserver.enums.CommonDomains
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.internalroutes.TestRoute
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


fun main() {
    Hirasawa.initDatabase(MysqlDatabase(Hirasawa.config.database))
    Hirasawa.packetRouter[BanchoPacketType.OSU_SEND_IRC_MESSAGE] = SendIrcMessagePacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_CHANNEL_JOIN] = ChannelJoinPacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_CHANNEL_LEAVE] = ChannelLeavePacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_EXIT] = ExitPacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_USER_STATS_REQUEST] = UserStatsRequestPacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_USER_PRESENCE_REQUEST] = UserPresenceRequestPacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_SEND_USER_STATS] = SendUserStatsPacket()

    for (channel in Hirasawa.config.channels) {
        Hirasawa.chatEngine[channel.name] = channel
    }

    Hirasawa.chatEngine.registerCommand(TestCommand())

    Hirasawa.banchoUsers.add(Hirasawa.hirasawaBot)

    // Timeout users every second
    val exec = Executors.newSingleThreadScheduledExecutor()
    exec.scheduleAtFixedRate(UserTimeoutThread(), 0, 1, TimeUnit.SECONDS)

    val webserver = Hirasawa.webserver

    webserver.addRoute(CommonDomains.OSU_WEB, "/", HttpMethod.GET, TestRoute())
    webserver.addRoute(CommonDomains.OSU_BANCHO,"/", HttpMethod.POST, BanchoRoute())
    webserver.addRoute(CommonDomains.OSU_WEB,"/web/osu-osz2-getscores.php", HttpMethod.GET, OsuOsz2GetScoresRoute())
    webserver.addRoute(CommonDomains.OSU_WEB,"/b/{beatmap}", HttpMethod.GET, BeatmapRoute())

    webserver.cloneRoutes(CommonDomains.OSU_WEB, Hirasawa.config.domain)

    val alternativeBancho = listOf("c1.ppy.sh", "c2.ppy.sh", "c3.ppy.sh", "c4.ppy.sh", "c5.ppy.sh",
        "c6.ppy.sh", "c7.ppy.sh", "c8.ppy.sh", "c9.ppy.sh", "ce.ppy.sh")

    for (alternative in alternativeBancho) {
        webserver.cloneRoutes(CommonDomains.OSU_BANCHO, alternative)
    }

    Hirasawa.pluginManager.loadPluginsFromDirectory(File("plugins"), true)

    webserver.start()


    // Hardcoded fake channel to get console responses
    val consoleChatChannel = ChatChannel("!CONSOLE", "", false)
    while (true) {
        Hirasawa.chatEngine.handleCommand(readLine()?.split(" ")!!, ConsoleCommandSender(), consoleChatChannel)
    }
}