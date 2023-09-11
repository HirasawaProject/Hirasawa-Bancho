package io.hirasawa.server.chat

data class ChatChannelMetadata(val name: String,
                               val description: String,
                               val autojoin: Boolean,
                               val canSeePermission: String? = null,
                               val canTalkPermission: String? = null)
