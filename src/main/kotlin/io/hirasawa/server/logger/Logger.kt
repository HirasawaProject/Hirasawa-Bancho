package io.hirasawa.server.logger

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


abstract class Logger: ILogger {
    private val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withLocale(Locale.UK)
        .withZone(ZoneId.systemDefault())
    internal val timestamp: String get() {
        return "[${formatter.format(Instant.now())}]"
    }
}