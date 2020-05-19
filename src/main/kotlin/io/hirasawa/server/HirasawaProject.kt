package io.hirasawa.server

import io.hirasawa.server.bancho.chat.command.ConsoleCommandSender
import io.hirasawa.server.bancho.packethandler.ChannelJoinPacket
import io.hirasawa.server.bancho.packethandler.ChannelLeavePacket
import io.hirasawa.server.bancho.packethandler.ExitPacket
import io.hirasawa.server.bancho.packethandler.SendIrcMessagePacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.threads.UserTimeoutThread
import io.hirasawa.server.bancho.user.HirasawaBot
import io.hirasawa.server.commands.TestCommand
import io.hirasawa.server.routes.BanchoRoute
import io.hirasawa.server.routes.test.*
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.routes.TestRoute
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


fun main() {
    Hirasawa.packetRouter[BanchoPacketType.OSU_SEND_IRC_MESSAGE] = SendIrcMessagePacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_CHANNEL_JOIN] = ChannelJoinPacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_CHANNEL_LEAVE] = ChannelLeavePacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_EXIT] = ExitPacket()

    for (channel in Hirasawa.config.channels) {
        Hirasawa.chatEngine[channel.name] = channel
    }

    Hirasawa.chatEngine.registerCommand(TestCommand())

    Hirasawa.banchoUsers.add(HirasawaBot(Hirasawa.database.getUser(2)))

    // Timeout users every second
    val exec = Executors.newSingleThreadScheduledExecutor()
    exec.scheduleAtFixedRate(UserTimeoutThread(), 0, 1, TimeUnit.SECONDS)

    val webserver = Hirasawa.webserver

    webserver.addRoute("/", HttpMethod.GET, TestRoute())
    webserver.addRoute("/", HttpMethod.POST, BanchoRoute())

    webserver.addRoute("/test/get", HttpMethod.GET, TestGetRoute())
    webserver.addRoute("/test/post", HttpMethod.POST, TestPostRoute())
    webserver.addRoute("/test/get/user", HttpMethod.GET, TestGetUserRoute())
    webserver.addRoute("/test/get/user/auth", HttpMethod.GET, TestGetUserAuthRoute())
    webserver.addRoute("/test/get/bancho/users", HttpMethod.GET, TestGetBanchoUsersRoute())
    webserver.addRoute("/test/get/passwordhash", HttpMethod.GET, TestGetPasswordHashRoute())
    webserver.addRoute("/test/error", HttpMethod.GET, TestErrorRoute())

    Hirasawa.pluginManager.loadPluginsFromDirectory(File("plugins"), true)

    webserver.start()

    while (true) {
        Hirasawa.chatEngine.handleCommand(readLine()?.split(" ")!!, ConsoleCommandSender())
    }
}