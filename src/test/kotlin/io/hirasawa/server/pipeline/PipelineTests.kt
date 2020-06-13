package io.hirasawa.server.pipeline

import io.hirasawa.server.Hirasawa
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.Exception
import java.nio.file.Files

class PipelineTests {
    @Test
    fun testPipelineRuns() {
        var pipelineRan = false
        Hirasawa.pipeline.queue(Runnable {
            pipelineRan = true
        })

        Thread.sleep(500) // make sure it gets the chance to run, not happy about sleeps in tests though
        assertTrue(pipelineRan)
    }

    @Test
    fun testPipelineHandlesErrors() {
        var secondaryPipelineRuns = false
        Hirasawa.pipeline.queue(Runnable {
            throw Exception("testPipelineHandlesErrors")
        })


        Hirasawa.pipeline.queue(Runnable {
            secondaryPipelineRuns = true
        })

        Thread.sleep(500) // make sure it gets the chance to run, not happy about sleeps in tests though
        assertTrue(secondaryPipelineRuns)

        // Parsing stack trace dumped to log file
        val logs = Files.readAllLines(File("logs/pipeline.txt").toPath())
        assertTrue(logs[logs.size - 3].contains("testPipelineHandlesErrors"))
    }
}