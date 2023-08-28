package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable

object FriendTable: LaravelTable("friend") {
    val userId = integer("user_id").references(UsersTable.id)
    val friendId = integer("friend_id")
}