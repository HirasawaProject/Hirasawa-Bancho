package io.hirasawa.server.plugin.internalplugins

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.packethandler.*
import io.hirasawa.server.bancho.packethandler.multiplayer.*
import io.hirasawa.server.bancho.packets.BanchoPacketType
import io.hirasawa.server.bancho.threads.UserTimeoutThread
import io.hirasawa.server.commands.*
import io.hirasawa.server.plugin.HirasawaPlugin
import io.hirasawa.server.plugin.PluginDescriptor
import io.hirasawa.server.routes.BanchoRoute
import io.hirasawa.server.webserver.enums.CommonDomains
import io.hirasawa.server.webserver.enums.HttpMethod
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class InternalBanchoPlugin: HirasawaPlugin() {
    private val threadExecutor = Executors.newSingleThreadScheduledExecutor()
    private val alternativeBanchoDomains = listOf("c.${Hirasawa.config.domain}", "c6.${Hirasawa.config.domain}")
    override fun onEnable() {
        // Large amount of duplicated code moved to own function
        registerPackets()
        registerCommands()

        // Setup chat channels
        for (channel in Hirasawa.config.channels) {
            Hirasawa.chatEngine[channel.name] = channel
        }

        // Add Hirasawa's "BanchoBot" to the player list
        // This allows users to see them as being online
        Hirasawa.banchoUsers.add(Hirasawa.hirasawaBot)

        // Timeout users every second
        threadExecutor.scheduleAtFixedRate(UserTimeoutThread(), 0, 1, TimeUnit.SECONDS)

        // Register web route for Bancho connections
        Hirasawa.webserver.addRoute(CommonDomains.OSU_BANCHO, "/", HttpMethod.POST, BanchoRoute())

        for (alternative in alternativeBanchoDomains) {
            Hirasawa.webserver.cloneRoutes(CommonDomains.OSU_BANCHO, alternative)
        }
    }

    override fun onDisable() {
        // Commands and events are automatically removed when disabling a plugin so we just need to work on
        // everything else
        threadExecutor.shutdown()
        Hirasawa.banchoUsers.remove(Hirasawa.hirasawaBot)
        // TODO add ability to remove web routes and registered packets

        Hirasawa.webserver.removeRoute(CommonDomains.OSU_BANCHO, "/", HttpMethod.POST)
    }

    private fun registerPackets() {
        Hirasawa.packetRouter[BanchoPacketType.OSU_SEND_IRC_MESSAGE] = SendIrcMessagePacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_SEND_IRC_MESSAGE_PRIVATE] = SendIrcMessagePrivatePacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_CHANNEL_JOIN] = ChannelJoinPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_CHANNEL_LEAVE] = ChannelLeavePacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_EXIT] = ExitPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_USER_STATS_REQUEST] = UserStatsRequestPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_USER_PRESENCE_REQUEST] = UserPresenceRequestPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_SEND_USER_STATS] = SendUserStatsPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_START_SPECTATING] = StartSpectatingPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_STOP_SPECTATING] = StopSpectatingPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_SPECTATE_FRAMES] = SpectateFramesPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_LOBBY_PART] = LobbyPartPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_LOBBY_JOIN] = LobbyJoinPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_CREATE] = MatchCreatePacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_CHANGE_SETTINGS] = MatchChangeSettingPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_CHANGE_SLOT] = MatchChangeSlotPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_LOCK] = MatchLockPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_READY] = MatchReadyPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_NOT_READY] = MatchNotReadyPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_CHANGE_MODS] = MatchChangeModsPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_JOIN] = MatchJoinPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_INVITE] = MatchInvitePacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_NO_BEATMAP] = MatchNoBeatmapPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_HAS_BEATMAP] = MatchHasBeatmapPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_START] = MatchStartPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_LOAD_COMPLETE] = MatchLoadCompletePacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_SCORE_UPDATE] = MatchScoreUpdatePacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_FAILED] = MatchFailedPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_COMPLETE] = MatchCompletePacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_PART] = MatchPartPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_TRANSFER_HOST] = MatchTransferHostPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_SKIP_REQUEST] = MatchSkipRequestPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_CHANGE_TEAM] = MatchChangeTeamPacket()
        Hirasawa.packetRouter[BanchoPacketType.OSU_MATCH_CHANGE_PASSWORD] = MatchChangePasswordPacket()
    }

    private fun registerCommands() {
        registerCommand(TestCommand())
        registerCommand(HelpCommand())
        registerCommand(PingCommand())
        registerCommand(ReloadCommand())
        registerCommand(RankCommand())
        registerCommand(ReportCommand())
        registerCommand(UserInfoCommand())
    }

    companion object {
        val descriptor = PluginDescriptor("Hirasawa Bancho", Hirasawa.version, "Hirasawa Contributors",
            "InternalBanchoPlugin")
    }
}