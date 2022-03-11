package io.hirasawa.server.config

import com.google.gson.*
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.plugin.PluginDescriptor
import io.hirasawa.server.update.SemVer
import java.lang.reflect.Type

class PluginDescriptorSerialiser: JsonDeserializer<PluginDescriptor>, JsonSerializer<PluginDescriptor> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PluginDescriptor {
        val primitive = json.asJsonObject
        return PluginDescriptor(primitive["name"].asString,
            SemVer.parse(primitive["version"].asString),
            primitive["author"].asString,
            primitive["main"].asString
        )
    }

    override fun serialize(pluginDescriptor: PluginDescriptor, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()

        jsonObject.addProperty("name", pluginDescriptor.name)
        jsonObject.addProperty("version", pluginDescriptor.version.toString())
        jsonObject.addProperty("author", pluginDescriptor.author)
        jsonObject.addProperty("main", pluginDescriptor.main)

        return jsonObject
    }
}