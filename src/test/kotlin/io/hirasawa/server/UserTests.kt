package io.hirasawa.server

import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.webserver.Helper.Companion.createUser
import io.hirasawa.server.webserver.Helper.Companion.createUserStats
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class UserTests {
    init {
        Hirasawa.initDatabase(memoryDatabase = true)
    }

    @Test
    fun canUserSpectateOtherUsers() {
        val user1 = createUser("CUSOU1")
        createUserStats(user1.id, GameMode.OSU)
        Hirasawa.banchoUsers.add(user1 as BanchoUser)

        val user2 = createUser("CUSOU2")
        createUserStats(user2.id, GameMode.OSU)
        Hirasawa.banchoUsers.add(user2 as BanchoUser)

        user1.spectateUser(user2)

        assert(user1.spectating == user2)
        assert(user2.spectators.contains(user1))
    }

    @Test
    fun canUserSpectateAndThenStopSpectatingOtherUsers() {
        val user1 = createUser("CUSOU1")
        createUserStats(user1.id, GameMode.OSU)
        Hirasawa.banchoUsers.add(user1 as BanchoUser)

        val user2 = createUser("CUSOU2")
        createUserStats(user2.id, GameMode.OSU)
        Hirasawa.banchoUsers.add(user2 as BanchoUser)

        user1.spectateUser(user2)

        assert(user1.spectating == user2)
        assert(user2.spectators.contains(user1))

        user1.stopSpectating()

        assert(user1.spectating == null)
        assertFalse(user2.spectators.contains(user1))
    }

    @Test
    fun canUserSpectateAnotherUserAndThenSpectateADifferentUser() {
        val user1 = createUser("CUSOU1")
        createUserStats(user1.id, GameMode.OSU)
        Hirasawa.banchoUsers.add(user1 as BanchoUser)

        val user2 = createUser("CUSOU2")
        createUserStats(user2.id, GameMode.OSU)
        Hirasawa.banchoUsers.add(user2 as BanchoUser)

        val user3 = createUser("CUSOU3")
        createUserStats(user2.id, GameMode.OSU)
        Hirasawa.banchoUsers.add(user3 as BanchoUser)

        user1.spectateUser(user2)

        assert(user1.spectating == user2)
        assert(user2.spectators.contains(user1))

        user1.spectateUser(user3)

        assert(user1.spectating == user3)
        assert(user3.spectators.contains(user1))
        assertFalse(user2.spectators.contains(user1))
    }
}