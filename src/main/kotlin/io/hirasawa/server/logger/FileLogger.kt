package io.hirasawa.server.logger

import java.io.File
import java.io.FileWriter

open class FileLogger(file: File): Logger() {
    private val writer = FileWriter(file, true)
    override fun log(message: Any) {
        writer.write("$timestamp $message\n")
        writer.flush()
    }
}