package io.hirasawa.server.chat.message

import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.bancho.user.User

class GlobalChatMessage(user: User, val channel: ChatChannel, message: String):
    ChatMessage(user, channel.name, message) {
}