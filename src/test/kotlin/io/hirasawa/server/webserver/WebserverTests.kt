package io.hirasawa.server.webserver

import io.hirasawa.server.logger.FileLogger
import io.hirasawa.server.webserver.enums.ContentType
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.AssetNode
import io.hirasawa.server.webserver.route.Route
import kotlinx.html.body
import kotlinx.html.p
import okhttp3.FormBody
import okhttp3.OkHttpClient
import org.bouncycastle.util.encoders.Base64
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import kotlin.math.log

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebserverTests {
    private val webserver = Webserver(8181, 0)
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
        val file = createTempFile()
        val logger = FileLogger(file)

        val logBefore = Files.readAllLines(file.toPath())
        logger.log("This is a test")
        val logAfter = Files.readAllLines(file.toPath())

        assert(logBefore.size < logAfter.size)
        assert("This is a test" in logAfter.last())
    }

    @Test
    fun doesAssetNodeWorkWithTextFiles() {
        val fileText = "This is a test text file"
        val tempFile = createTempFile()
        Files.write(tempFile.toPath(), fileText.toByteArray())

        webserver.addAsset("localhost", "/asset/text", HttpMethod.GET, tempFile.absolutePath)

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/asset/text")
            .build()

        val response = client.newCall(request).execute()

        assertEquals(fileText, response.body?.string())
        assertEquals(HttpStatus.OK.code, response.code)
        assertEquals(ContentType.TEXT_PLAN.toString(), response.headers["content-type"])
    }

    @Test
    fun doesAssetNodeWorkWithPngFiles() {
        // Data taken from https://png-pixel.com/
        val pngData = Base64.decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQA" +
                "AAABJRU5ErkJggg==")
        val tempFile = createTempFile()
        Files.write(tempFile.toPath(), pngData)

        webserver.addAsset("localhost", "/asset/png", HttpMethod.GET, tempFile.absolutePath)

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/asset/png")
            .build()

        val response = client.newCall(request).execute()

        assertEquals(String(pngData), response.body?.string())
        assertEquals(HttpStatus.OK.code, response.code)
        assertEquals(ContentType.IMAGE_PNG.toString(), response.headers["content-type"])
    }

    @Test
    fun doesAccessingRouteIncreaseAccessLog() {
        webserver.addRoute("localhost", "/logtestenabled", HttpMethod.GET, object:
            Route {
            override fun handle(request: Request, response: Response) {
                // empty
            }
        })

        val accessLogBefore = Files.readAllLines(File("logs/webserver/access.txt").toPath())

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/logtestenabled")
            .build()

        val response = client.newCall(request).execute()

        val accessLogAfter = Files.readAllLines(File("logs/webserver/access.txt").toPath())

        assertEquals(HttpStatus.OK.code, response.code)
        assert(accessLogBefore.size < accessLogAfter.size)
    }

    @Test
    fun doesAccessingRouteWithDisabledLoggingNotIncreaseAccessLog() {
        webserver.addRoute("localhost", "/logtestdisabled", HttpMethod.GET, object:
            Route {
            override fun handle(request: Request, response: Response) {
                response.isLoggingEnabled = false
            }
        })

        val accessLogBefore = Files.readAllLines(File("logs/webserver/access.txt").toPath())

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/logtestdisabled")
            .build()

        val response = client.newCall(request).execute()

        val accessLogAfter = Files.readAllLines(File("logs/webserver/access.txt").toPath())

        assertEquals(HttpStatus.OK.code, response.code)
        assertEquals(accessLogBefore.size, accessLogAfter.size)
    }

    @Test
    fun doesHtmlDslWork() {
        webserver.addRoute("localhost", "/htmldsl", HttpMethod.GET, object:
            Route {
            override fun handle(request: Request, response: Response) {
                response.writeRawHtml {
                    body {
                        p {
                            text("Hello world")
                        }
                    }
                }
            }
        })


        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/htmldsl")
            .build()

        val response = client.newCall(request).execute()

        val body = response.body?.string()!!

        assertEquals(HttpStatus.OK.code, response.code)
        assertEquals("73", response.headers["Content-Size"])
        assert(body.contains("<!DOCTYPE html>"))
        assert(body.contains("<html>"))
        assert(body.contains("<body>"))
        assert(body.contains("<p>"))
        assert(body.contains("Hello world"))
    }

    @Test
    fun testIpAddressCanBeChangedViaWebRequestEvent() {
        webserver.addRoute("localhost", "/ipaddress", HttpMethod.GET, object:
            Route {
            override fun handle(request: Request, response: Response) {
                request.ipAddress = "test"
                response.writeText(request.ipAddress)
            }
        })

        val accessLogBefore = Files.readAllLines(File("logs/webserver/access.txt").toPath())

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8181/ipaddress")
            .build()

        val response = client.newCall(request).execute()

        val body = response.body?.string()!!

        val accessLogAfter = Files.readAllLines(File("logs/webserver/access.txt").toPath())

        assertEquals(HttpStatus.OK.code, response.code)
        assertEquals("4", response.headers["Content-Size"])
        assertEquals("test", body)
        assertEquals(1, accessLogAfter.size - accessLogBefore.size)
        assert(body.contains("test"))
    }
}