package io.hirasawa.server.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

open class LaravelTable(name: String): IntIdTable(name) {
    val createdAt = timestamp("created_at").default(Instant.now())
    val updatedAt = timestamp("update_at").nullable().clientDefault { Instant.now() }
}