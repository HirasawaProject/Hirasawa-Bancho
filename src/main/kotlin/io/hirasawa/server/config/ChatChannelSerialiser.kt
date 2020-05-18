package io.hirasawa.server.config

import com.google.gson.*
import io.hirasawa.server.bancho.chat.ChatChannel
import java.lang.reflect.Type

class ChatChannelSerialiser: JsonDeserializer<ChatChannel>, JsonSerializer<ChatChannel> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ChatChannel {
        val primitive = json.asJsonObject
        return ChatChannel(primitive["name"].asString,
            primitive["description"].asString,
            primitive["autojoin"].asBoolean)
    }

    override fun serialize(chatChannel: ChatChannel, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        jsonObject.addProperty("name", chatChannel.name)
        jsonObject.addProperty("description", chatChannel.description)
        jsonObject.addProperty("autojoin", chatChannel.autojoin)

        return jsonObject
    }
}