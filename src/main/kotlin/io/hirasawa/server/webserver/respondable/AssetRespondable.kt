package io.hirasawa.server.webserver.respondable

import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.enums.HttpStatus
import io.hirasawa.server.webserver.exceptions.HttpException
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import java.io.File
import java.nio.file.Files

class AssetRespondable(private val assetPath: String): HttpRespondable {
    override fun respond(request: Request, response: Response) {
        if (File(assetPath).exists()) {
            val filePath = File(assetPath).toPath()
            val mimeType = Files.probeContentType(filePath)

            response.headers[HttpHeader.CONTENT_TYPE] = mimeType

            val bytes = Files.readAllBytes(filePath)

            response.headers[HttpHeader.CONTENT_LENGTH] = bytes.size

            for (i in 0..bytes.size) {
                if (i % 1000 == 0) {
                    response.flush()
                }
                response.outputStream.writeByte(bytes[i].toInt())
            }
        } else {
            throw HttpException(HttpStatus.NOT_FOUND)
        }
    }
}