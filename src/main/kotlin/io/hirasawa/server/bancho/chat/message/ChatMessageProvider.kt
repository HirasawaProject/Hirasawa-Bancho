package io.hirasawa.server.bancho.chat.message

import io.hirasawa.server.bancho.user.User

class ChatMessageProvider(source: User, destinationName: String, message: String): ChatMessage(source, destinationName,
    message)