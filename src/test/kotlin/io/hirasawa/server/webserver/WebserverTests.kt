package io.hirasawa.server.webserver

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
import java.lang.Exception

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebserverTests {
    private val webserver = Webserver(8080)
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
            .url("http://localhost:8080/getparams?foo=bar&bar=baz")
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
            .url("http://localhost:8080/postparams")
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

        val request = okhttp3.Request.Builder()
            .url("http://localhost:8080/error")
            .build()

        val response = client.newCall(request).execute()

        val body = response.body?.string()!!

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, response.code)
        assertEquals("185", response.headers["Content-Size"])
        assert(body.contains("Internal Server Error"))
        assert(body.contains("GET (/error)"))
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
            .url("http://localhost:8080/params/101/Hirasawa")
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
            .url("http://localhost:8080/params/101")
            .build()

        val response = client.newCall(request).execute()

        val body = response.body?.string()!!

        assertEquals(HttpStatus.BAD_REQUEST.code, response.code)
        assertEquals("198", response.headers["Content-Size"])
        assert(body.contains("Bad Request"))
        assert(body.contains("GET (/params/101)"))
    }
}