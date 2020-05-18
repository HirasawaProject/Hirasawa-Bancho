package io.hirasawa.server

import io.hirasawa.server.database.DatabaseCredentials

data class HirasawaConfig (val httpPort: Int, val httpsPort: Int, val database: DatabaseCredentials)