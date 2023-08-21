package io.hirasawa.server.chat.channel

import io.hirasawa.server.bancho.objects.MultiplayerMatch
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.ChatChannelMetadata

class MultiplayerChannel(multiplayerMatch: MultiplayerMatch): ChatChannel(
    ChatChannelMetadata("#multiplayer", "#multiplayer community chat", false),
    multiplayerMatch.users)