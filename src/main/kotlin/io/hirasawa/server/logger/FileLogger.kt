package io.hirasawa.server.logger

import java.io.File
import java.io.FileWriter

open class FileLogger(file: File): Logger {
    private val writer = FileWriter(file)
    override fun log(message: Any) {
        writer.write(message.toString())
        writer.flush()
    }
}