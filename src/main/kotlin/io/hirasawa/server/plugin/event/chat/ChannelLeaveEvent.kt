package io.hirasawa.server.plugin.event.chat

import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.bancho.user.User
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when a user leaves a chat channel
 */
class ChannelLeaveEvent(val user: User, val chatChannel: ChatChannel): HirasawaEvent<ChannelLeaveEvent>, Cancelable()