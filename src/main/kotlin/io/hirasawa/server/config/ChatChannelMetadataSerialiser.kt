package io.hirasawa.server.config

import com.google.gson.*
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.chat.ChatChannelMetadata
import java.lang.reflect.Type

class ChatChannelMetadataSerialiser: JsonDeserializer<ChatChannelMetadata>, JsonSerializer<ChatChannelMetadata> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ChatChannelMetadata {
        val primitive = json.asJsonObject
        return ChatChannelMetadata(primitive["name"].asString,
            primitive["description"].asString,
            primitive["autojoin"].asBoolean)
    }

    override fun serialize(metadata: ChatChannelMetadata, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        jsonObject.addProperty("name", metadata.name)
        jsonObject.addProperty("description", metadata.description)
        jsonObject.addProperty("autojoin", metadata.autojoin)

        return jsonObject
    }
}