package io.hirasawa.server.bancho.packets

import io.hirasawa.server.bancho.user.User

class FriendsListPacket(friends: ArrayList<User>):
        BanchoPacket(BanchoPacketType.BANCHO_FRIENDS_LIST) {
    init {
        writer.writeShort(friends.size.toShort())
        for (user in friends) {
            writer.writeInt(user.id)
        }
    }
}