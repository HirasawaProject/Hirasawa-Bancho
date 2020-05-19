package io.hirasawa.server.bancho.serialisation

import io.hirasawa.server.bancho.io.OsuWriter

interface SerialisedBanchoObject {
    fun write(writer: OsuWriter)
}