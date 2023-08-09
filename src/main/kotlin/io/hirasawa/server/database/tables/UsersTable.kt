package io.hirasawa.server.database.tables

import io.hirasawa.server.database.LaravelTable
import org.jetbrains.exposed.sql.javatime.timestamp

object UsersTable: LaravelTable("users") {
    val username = varchar("username", 30)
    val email = varchar("email", 255)
    val emailed_verified_at = timestamp("email_verified_at").nullable()
    val password = varchar("password", 60)
    val rememberToken = varchar("remember_token", 255).nullable()
    val ircToken = varchar("irc_token", 32).nullable()
    val isBanned = bool("is_banned")
}