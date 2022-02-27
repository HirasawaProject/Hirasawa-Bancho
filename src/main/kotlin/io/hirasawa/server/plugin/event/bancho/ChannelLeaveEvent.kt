package io.hirasawa.server.plugin.event.bancho

import io.hirasawa.server.bancho.chat.ChatChannel
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when player leaves a chat channel
 */
class ChannelLeaveEvent(val banchoUser: BanchoUser, val chatChannel: ChatChannel): HirasawaEvent<ChannelLeaveEvent>, Cancelable()