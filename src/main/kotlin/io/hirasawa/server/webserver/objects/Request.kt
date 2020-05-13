package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.HttpMethod

data class Request(
    val path: String,
    val httpMethod: HttpMethod,
    val headers: HashMap<String, String>
)