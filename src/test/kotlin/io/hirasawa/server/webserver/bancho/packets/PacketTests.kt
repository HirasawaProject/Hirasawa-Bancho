package io.hirasawa.server.webserver.bancho.packets

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.objects.UserStats
import io.hirasawa.server.bancho.packethandler.SendUserStatsPacket
import io.hirasawa.server.database.MemoryDatabase
import io.hirasawa.server.webserver.Helper.Companion.createUser
import io.hirasawa.server.webserver.Helper.Companion.createUserStats
import io.hirasawa.server.webserver.Helper.Companion.userToBanchoUser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*

class PacketTests {
    val database = MemoryDatabase()
    init {
        Hirasawa.initDatabase(database)
    }

    @Test
    fun testDoesChangingGamemodeSwitchUserStats() {
        val user = createUser(1, "Username")
        val osuStats = createUserStats(1, GameMode.OSU)
        val taikoStats = createUserStats(1, GameMode.TAIKO)

        database.users.add(user)
        database.userStats[user.id] = EnumMap(GameMode::class.java)
        database.userStats[user.id]?.set(GameMode.OSU, osuStats)
        database.userStats[user.id]?.set(GameMode.TAIKO, taikoStats)

        val payload = ByteArrayOutputStream()
        val writer = OsuWriter(payload)

        writer.writeByte(0) // Status
        writer.writeString("") // Status text
        writer.writeString("") // Beatmap checksum
        writer.writeInt(0) // Mods
        writer.writeByte(GameMode.TAIKO.ordinal.toByte()) // Gamemode
        writer.writeInt(0) // Beatmap ID

        payload.close()

        val banchoUser = userToBanchoUser(user)

        assertEquals(GameMode.OSU, banchoUser.userStats.gameMode)

        val input = ByteArrayInputStream(payload.toByteArray())
        val reader = OsuReader(input)

        val packet = SendUserStatsPacket()
        packet.handle(reader, writer, banchoUser)

        assertEquals(GameMode.TAIKO, banchoUser.status.mode)


    }
}