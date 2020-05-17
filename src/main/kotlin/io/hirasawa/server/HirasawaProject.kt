package io.hirasawa.server

import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.packethandler.ChannelJoinPacket
import io.hirasawa.server.bancho.packethandler.ChannelLeavePacket
import io.hirasawa.server.bancho.packethandler.SendIrcMessagePacket
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.packets.ChannelListingCompletePacket
import io.hirasawa.server.commands.TestCommand
import io.hirasawa.server.routes.BanchoRoute
import io.hirasawa.server.routes.test.*
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.routes.TestRoute
import java.io.File


fun main() {
    Hirasawa.packetRouter[BanchoPacketType.OSU_SEND_IRC_MESSAGE] = SendIrcMessagePacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_CHANNEL_JOIN] = ChannelJoinPacket()
    Hirasawa.packetRouter[BanchoPacketType.OSU_CHANNEL_LEAVE] = ChannelLeavePacket()

    Hirasawa.chatEngine["#osu"] = ChatChannel("#osu", "Main channel", true)
    Hirasawa.chatEngine["#hirasawa"] = ChatChannel("#hirasawa", "Talk about the Hirasawa Project", true)
    Hirasawa.chatEngine["#idolhell"] = ChatChannel("#idolhell", "Talk about Love Live", false)

    Hirasawa.chatEngine.registerCommand(TestCommand())

    val webserver = Hirasawa.webserver

    webserver.addRoute("/", HttpMethod.GET, TestRoute())
    webserver.addRoute("/", HttpMethod.POST, BanchoRoute())

    webserver.addRoute("/test/get", HttpMethod.GET, TestGetRoute())
    webserver.addRoute("/test/post", HttpMethod.POST, TestPostRoute())
    webserver.addRoute("/test/get/user", HttpMethod.GET, TestGetUserRoute())
    webserver.addRoute("/test/get/user/auth", HttpMethod.GET, TestGetUserAuthRoute())
    webserver.addRoute("/test/get/passwordhash", HttpMethod.GET, TestGetPasswordHashRoute())
    webserver.addRoute("/test/error", HttpMethod.GET, TestErrorRoute())

    Hirasawa.pluginManager.loadPluginsFromDirectory(File("plugins"))

    webserver.start()
}