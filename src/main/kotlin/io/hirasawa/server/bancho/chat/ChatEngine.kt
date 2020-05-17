package io.hirasawa.server.bancho.chat

class ChatEngine {
    val chatChannels = HashMap<String, ChatChannel>()

    operator fun set(key: String, value: ChatChannel) {
        chatChannels[key] = value
    }

    operator fun get(key: String): ChatChannel? {
        return chatChannels[key]
    }
}