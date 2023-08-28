package io.hirasawa.server.chat.channel

import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.ChatChannelMetadata

class SpectatorChannel(user: BanchoUser): ChatChannel(
    ChatChannelMetadata("#spectator", "#spectator community chat", true),
    user.spectators) {

    init {
        // The user being spectated should probably see their own spectator channel
        addUser(user)
    }
}