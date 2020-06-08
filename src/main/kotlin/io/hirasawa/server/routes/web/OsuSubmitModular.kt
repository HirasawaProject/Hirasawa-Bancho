package io.hirasawa.server.routes.web

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.handlers.ScoreHandler
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.internalroutes.errors.RouteForbidden
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.route.Route
import org.bouncycastle.crypto.InvalidCipherTextException
import org.bouncycastle.crypto.engines.RijndaelEngine
import org.bouncycastle.crypto.modes.CBCBlockCipher
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher
import org.bouncycastle.crypto.paddings.ZeroBytePadding
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.crypto.params.ParametersWithIV
import org.bouncycastle.util.encoders.Base64


class OsuSubmitModular: Route {
    override fun handle(request: Request, response: Response) {
        if (request.headers[HttpHeader.USER_AGENT] != "osu!") {
            RouteForbidden().handle(request, response)
            return
        }

        val iv = Base64.decode(request.post["iv"])
        val encodedScore = Base64.decode(request.post["score"])
        val key = "osu!-scoreburgr---------${request.post["osuver"]}".toByteArray()

        val decryptedScore = decrypt(key, iv, encodedScore)

        val handler = ScoreHandler(decryptedScore).score

        if (handler?.user == null) {
            response.writeText("error: pass")
            return
        }

        if (Hirasawa.database.authenticateWithMd5(handler.user.username, request.post["pass"] ?: "")) {
            // Add to database

        } else {
            response.writeText("error: pass")
        }
    }



    fun decrypt(key: ByteArray, iv: ByteArray, data: ByteArray): String {
        val aes = PaddedBufferedBlockCipher(CBCBlockCipher(RijndaelEngine(256)), ZeroBytePadding())
        aes.init(false, ParametersWithIV(KeyParameter(key), iv))
        return String(cipherData(aes, data)!!)
    }

    /**
     * Stolen from https://stackoverflow.com/questions/8083144/how-to-encrypt-or-decrypt-with-rijndael-and-a-block-size-of-256-bits
     */
    @Throws(InvalidCipherTextException::class)
    private fun cipherData(cipher: PaddedBufferedBlockCipher, data: ByteArray): ByteArray? {
        val minSize = cipher.getOutputSize(data.size)
        val outBuf = ByteArray(minSize)
        val length1 = cipher.processBytes(data, 0, data.size, outBuf, 0)
        val length2 = cipher.doFinal(outBuf, length1)
        val actualLength = length1 + length2
        val cipherArray = ByteArray(actualLength)
        for (x in 0 until actualLength) {
            cipherArray[x] = outBuf[x]
        }
        return cipherArray
    }
}