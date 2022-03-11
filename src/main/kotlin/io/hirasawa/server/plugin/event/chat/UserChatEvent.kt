package io.hirasawa.server.plugin.event.chat

import io.hirasawa.server.chat.message.ChatMessage
import io.hirasawa.server.plugin.event.Cancelable
import io.hirasawa.server.plugin.event.HirasawaEvent

/**
 * Event when a user sends a message in a chat channel
 */
class UserChatEvent(val chatMessage: ChatMessage): HirasawaEvent<UserChatEvent>, Cancelable()