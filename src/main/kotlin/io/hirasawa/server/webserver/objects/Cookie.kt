package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.SameSite
import java.time.Instant

data class Cookie(val value: String,
                  val expires: Instant? = null,
                  val secure: Boolean = true,
                  val httpOnly: Boolean = true,
                  val sameSite: SameSite = SameSite.STRICT,
                  val domain: String? = null,
                  val path: String? = null) {

    fun encode(): String {
        var encodedValue = value
        if (expires != null) {
            encodedValue += "; $expires"
        }
        if (secure) {
            encodedValue += "; Secure"
        }
        if (httpOnly) {
            encodedValue += "; HttpOnly"
        }
        encodedValue += "; SameSite=$sameSite"
        if (domain != null) {
            encodedValue += "; Domain=$domain"
        }
        if (path != null) {
            encodedValue += "; Path=$path"
        }

        return encodedValue
    }
}