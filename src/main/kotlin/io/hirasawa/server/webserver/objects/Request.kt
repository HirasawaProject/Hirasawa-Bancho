package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.HttpMethod
import io.hirasawa.server.webserver.handlers.ParameterHandler
import java.io.ByteArrayInputStream

data class Request(
    val urlSegment: UrlSegment,
    val httpMethod: HttpMethod,
    val headers: HashMap<String, String>,
    val byteArrayInputStream: ByteArrayInputStream
) {
    val post: HashMap<String, String> by lazy {
        ParameterHandler(byteArrayInputStream.readAllBytes()).parameters
    }

    val path: String
        get() = urlSegment.route

    val get: HashMap<String, String>
        get() = urlSegment.params
}