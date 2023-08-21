package io.hirasawa.server.chat

import io.hirasawa.server.chat.enums.ChatChannelVisibility

class GlobalChatChannel(metadata: ChatChannelMetadata): ChatChannel(metadata, visibility = ChatChannelVisibility.PUBLIC)