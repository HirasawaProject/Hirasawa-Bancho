package io.hirasawa.server.webserver.objects

import io.hirasawa.server.webserver.enums.HttpStatus
import java.io.DataOutputStream

data class Response (var httpStatus: HttpStatus, val outputStream: DataOutputStream,
                     val headers: HashMap<String, String> ) {
    fun writeText(text: String) {
        outputStream.writeBytes(text)
    }
}