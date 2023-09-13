package io.hirasawa.server.chat

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.webserver.Helper.Companion.createUser
import org.junit.jupiter.api.Test

class ChatEnginePermissionTests {
    init {
        Hirasawa.initDatabase(memoryDatabase = true)
    }

    @Test
    fun canUserSeeChannelTheyHavePermissionFor() {
        val user = createUser("cuscthpf", permissions = arrayListOf("cuscthpf"))
        assert(GlobalChatChannel(
            ChatChannelMetadata(
                "#cuscthpf",
                "cuscthpf",
                false,
                "cuscthpf",
                null
            )
        ).canUserSee(user))
    }

    @Test
    fun canUserSeeChannelTheyDontHavePermissionFor() {
        val user = createUser("cusctdhpf")
        assert(!GlobalChatChannel(
            ChatChannelMetadata(
                "#cusctdhpf",
                "cusctdhpf",
                false,
                "cusctdhpf",
                null
            )
        ).canUserSee(user))
    }

    @Test
    fun canUserSeeChannelWithNullPermissions() {
        val user = createUser("cuscthnpp")
        assert(GlobalChatChannel(
            ChatChannelMetadata(
                "#cuscthnpp",
                "cuscthnpp",
                false,
                null,
                null
            )
        ).canUserSee(user))
    }
}