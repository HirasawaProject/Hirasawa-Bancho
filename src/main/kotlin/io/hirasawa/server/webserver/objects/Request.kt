package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.HttpMethod
import java.io.ByteArrayInputStream

data class Request(
    val path: String,
    val httpMethod: HttpMethod,
    val headers: HashMap<String, String>,
    val byteArrayInputStream: ByteArrayInputStream
)