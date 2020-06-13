package io.hirasawa.server.pipeline

import io.hirasawa.server.logger.FileLogger
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception
import java.util.*

class PipelineThread(private val runnables: Stack<Runnable>, private val errorLogger: FileLogger): Thread() {
    override fun run() {
        while (true) {
            if (runnables.isNotEmpty()) {
                val runnable = runnables.pop()
                try {
                    runnable.run()
                } catch (e: Exception) {
                    val stringWriter = StringWriter()
                    e.printStackTrace(PrintWriter(stringWriter))
                    errorLogger.log(stringWriter)
                }
            }
        }
    }
}