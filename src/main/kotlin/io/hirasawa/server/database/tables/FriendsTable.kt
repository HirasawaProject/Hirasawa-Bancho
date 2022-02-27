package io.hirasawa.server.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object FriendsTable: IntIdTable("friends") {
    val userId = integer("user_id")
    val friendId = integer("friend_id").references(UsersTable.id)
}