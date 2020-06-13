package io.hirasawa.server.pipeline

import io.hirasawa.server.logger.FileLogger
import java.io.File
import java.util.*

class PipelineManager {
    private val runnables = Stack<Runnable>()
    private val errorLogger = FileLogger(File("logs/pipeline.txt"))
    init {
        PipelineThread(runnables, errorLogger).start()
    }

    fun queue(runnable: Runnable) {
        runnables.add(runnable)
    }

}