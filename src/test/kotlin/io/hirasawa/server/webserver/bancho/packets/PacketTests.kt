package io.hirasawa.server.webserver.bancho.packets

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.Mode
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packethandler.SendUserStatsPacket
import io.hirasawa.server.webserver.Helper.Companion.createUser
import io.hirasawa.server.webserver.Helper.Companion.createUserStats
import io.hirasawa.server.webserver.Helper.Companion.userToBanchoUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class PacketTests {
    init {
        Hirasawa.initDatabase(memoryDatabase = true)
    }

    @Test
    fun testDoesChangingGamemodeSwitchUserStats() {
        val user = createUser("DCGSUS")
        createUserStats(user.id, Mode.OSU)
        createUserStats(user.id, Mode.TAIKO)

        val payload = ByteArrayOutputStream()
        val writer = OsuWriter(payload)

        writer.writeByte(0) // Status
        writer.writeString("") // Status text
        writer.writeString("") // Beatmap checksum
        writer.writeInt(0) // Mods
        writer.writeByte(Mode.TAIKO.ordinal.toByte()) // Gamemode
        writer.writeInt(0) // Beatmap ID

        payload.close()

        val banchoUser = userToBanchoUser(user)

        assertEquals(Mode.OSU, banchoUser.userStats.mode)

        val input = ByteArrayInputStream(payload.toByteArray())
        val reader = OsuReader(input)

        val packet = SendUserStatsPacket()
        packet.handle(reader, writer, banchoUser)

        assertEquals(Mode.TAIKO, banchoUser.status.mode)


    }
}