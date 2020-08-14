package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.SameSite
import java.time.Instant

data class Cookie(val value: String,
                  val expires: Instant? = null,
                  val secure: Boolean = true,
                  val httpOnly: Boolean = true,
                  val sameSite: SameSite = SameSite.STRICT,
                  val domain: String? = null,
                  val path: String? = null)