package io.hirasawa.server.webserver

import io.hirasawa.server.logger.FileLogger
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import kotlin.math.log

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebserverTests {
    private val webserver = Webserver(8181)
    private var client = OkHttpClient()
    init {
        webserver.start()
    }

    @Test
    fun testDoesWebserverParseGetParams() {
        webserver.addRoute("localhost", "/getparams", HttpMethod.GET, object:
            Route {
            override fun handle(request: Request, response: Response) {
                response.writeText(request.get.toString())
            }
        })

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/getparams?foo=bar&bar=baz")
            .build()

        val response = client.newCall(request).execute()

        assertEquals(HttpStatus.OK.code, response.code)
        assertEquals("{bar=baz, foo=bar}", response.body?.string())
    }

    @Test
    fun testDoesWebserverParsePostParams() {
        webserver.addRoute("localhost", "/postparams", HttpMethod.POST, object :
            Route {
            override fun handle(request: Request, response: Response) {
                response.writeText(request.post.toString())
            }
        })

        val formBody = FormBody.Builder()
            .add("foo", "bar")
            .add("bar", "baz")
            .build()

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/postparams")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()

        assertEquals(HttpStatus.OK.code, response.code)
        assertEquals("{bar=baz, foo=bar}", response.body?.string())
    }

    @Test
    fun testDoesThrownErrorGiveErrorRoute() {
        webserver.addRoute("localhost", "/error", HttpMethod.GET, object :
            Route {
            override fun handle(request: Request, response: Response) {
                throw Exception("foo")
            }
        })

        val errorLogBefore = Files.readAllLines(File("logs/webserver/error.txt").toPath())

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/error")
            .build()

        val response = client.newCall(request).execute()

        val body = response.body?.string()!!

        val errorLogAfter = Files.readAllLines(File("logs/webserver/error.txt").toPath())

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.code)
        assertEquals("185", response.headers["Content-Size"])
        assert(body.contains("Internal Server Error"))
        assert(body.contains("GET (/error)"))
        assert(errorLogBefore.size < errorLogAfter.size)
    }

    @Test
    fun testDoesWebserverParseParameterisedRouteNodes() {
        webserver.addRoute("localhost", "/params/{number}/{name}", HttpMethod.GET, object:
            Route {
            override fun handle(request: Request, response: Response) {
                response.writeText(request.routeParameters.toString())
            }
        })

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/params/101/Hirasawa")
            .build()

        val response = client.newCall(request).execute()

        assertEquals(HttpStatus.OK.code, response.code)
        assertEquals("{number=101, name=Hirasawa}", response.body?.string())
    }

    @Test
    fun testDoesWebserverErrorOnIncorrectParameterisedRouteNode() {
        webserver.addRoute("localhost", "/params/{number}/{name}", HttpMethod.GET, object:
            Route {
            override fun handle(request: Request, response: Response) {
                response.writeText(request.routeParameters.toString())
            }
        })

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/params/101")
            .build()

        val response = client.newCall(request).execute()

        val body = response.body?.string()!!

        assertEquals(HttpStatus.BAD_REQUEST.code, response.code)
        assertEquals("198", response.headers["Content-Size"])
        assert(body.contains("Bad Request"))
        assert(body.contains("GET (/params/101)"))
    }

    @Test
    fun doesLogIncreaseWhenLogging() {
        val file = "test/doesLogIncreaseWhenLogging.txt"
        val logger = FileLogger(File(file))

        val logBefore = Files.readAllLines(File(file).toPath())
        logger.log("This is a test")
        val logAfter = Files.readAllLines(File(file).toPath())

        assert(logBefore.size < logAfter.size)
        assert("This is a test" in logAfter.last())
    }
}