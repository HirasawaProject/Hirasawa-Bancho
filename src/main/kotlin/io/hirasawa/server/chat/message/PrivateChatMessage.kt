package io.hirasawa.server.chat.message

import io.hirasawa.server.bancho.user.User

class PrivateChatMessage(source: User, val destination: User, message: String):
    ChatMessage(source, destination.username, message) {
}