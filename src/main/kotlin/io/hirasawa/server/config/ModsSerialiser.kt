package io.hirasawa.server.config

import com.google.gson.*
import io.hirasawa.server.chat.ChatChannel
import io.hirasawa.server.enums.Mod
import io.hirasawa.server.objects.Mods
import java.lang.reflect.Type

class ModsSerialiser: JsonDeserializer<Mods>, JsonSerializer<Mods> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Mods {
        val array = json.asJsonArray
        var mods = Mods()
        for (item in array) {
            mods += Mod.valueOf(item.asString)
        }

        return mods
    }

    override fun serialize(mods: Mods, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val array = JsonArray()
        for (mod: Mod in mods) {
            array.add(mod.name)
        }
        return array
    }
}