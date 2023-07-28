package io.hirasawa.server.routes

import io.hirasawa.server.Hirasawa
import io.hirasawa.server.bancho.enums.GameMode
import io.hirasawa.server.bancho.handler.BanchoLoginHandler
import io.hirasawa.server.bancho.io.OsuReader
import io.hirasawa.server.bancho.io.OsuWriter
import io.hirasawa.server.bancho.packets.*
import io.hirasawa.server.bancho.user.BanchoUser
import io.hirasawa.server.database.tables.UsersTable
import io.hirasawa.server.plugin.event.bancho.BanchoUserLoginEvent
import io.hirasawa.server.plugin.event.bancho.enums.BanchoLoginCancelReason
import io.hirasawa.server.polyfill.readNBytes
import io.hirasawa.server.webserver.route.Route
import io.hirasawa.server.webserver.enums.ContentType
import io.hirasawa.server.webserver.enums.HttpHeader
import io.hirasawa.server.webserver.objects.Request
import io.hirasawa.server.webserver.objects.Response
import io.hirasawa.server.webserver.internalroutes.errors.RouteForbidden
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.ByteArrayInputStream
import java.util.*

class BanchoRoute: Route {
    override fun handle(request: Request, response: Response) {
        response.isLoggingEnabled = false
        if (request.headers["User-Agent"] != "osu!") {
            // Only osu! should be able to contact Bancho
            // Tell RouteForbidden to handle this request for us
            RouteForbidden().handle(request, response)
            return
        }

        response.headers[HttpHeader.CONTENT_TYPE] = ContentType.APPLICATION_OCTET_STREAM
        response.headers["cho-version"] = "19"

        val osuWriter = OsuWriter(response.outputStream)
        val osuReader = OsuReader(request.inputStream)

        if ("osu-token" !in request.headers.keys()) {
            // No osu! token, we need to read the login information from the client
            // This is oddly not a standard packet so we need to handle it as a one-off
            val token = UUID.randomUUID()
            response.headers["cho-token"] = token

            val userInfo = BanchoLoginHandler(request.inputStream)

            if (Hirasawa.authenticate(userInfo.username, userInfo.password)) {
                val user = BanchoUser(transaction {
                    UsersTable.select { UsersTable.username eq userInfo.username }.first()
                })
                user.updateUserStats(GameMode.OSU)
                user.uuid = token
                user.updateKeepAlive()

                val cancelReason = if (user.isBanned) BanchoLoginCancelReason.BANNED  else BanchoLoginCancelReason.UNKNOWN
                BanchoUserLoginEvent(user, cancelReason).call().then {
                    Hirasawa.banchoUsers.add(user)
                    LoginReplyPacket(user.id).write(osuWriter)
                    ProtocolNegotiationPacket(19).write(osuWriter)
                    FriendsListPacket(user.friends).write(osuWriter)
                    LoginPermissionsPacket(user).write(osuWriter)

                    Hirasawa.chatEngine["#osu"]?.addUser(user)

                    for ((_, channel) in Hirasawa.chatEngine.chatChannels) {
                        if (channel.autojoin) {
                            ChannelAvailableAutojoinPacket(channel).write(osuWriter)
                        } else {
                            ChannelAvailablePacket(channel).write(osuWriter)
                        }
                    }
                    ChannelListingCompletePacket().write(osuWriter)
                    ChannelJoinSuccessPacket(Hirasawa.chatEngine["#osu"]!!).write(osuWriter)

                    UserPresenceBundlePacket(Hirasawa.banchoUsers.idKeys.toList()).write(osuWriter)
                    Hirasawa.sendBanchoPacketToAll(UserPresenceSinglePacket(user.id))

                    HandleOsuUpdatePacket(user).write(osuWriter)
                    UserPresencePacket(user).write(osuWriter)

                    if (user.hasPermission("hirasawa.update.notify") && Hirasawa.isUpdateRequired) {
                        user.sendPrivateMessage(Hirasawa.banchoBot, "You are running an outdated version of " +
                                "Hirasawa, please update by going to the following link")
                        user.sendPrivateMessage(Hirasawa.banchoBot,
                            Hirasawa.updateChecker.latestRelease?.assets?.first()?.browserDownloadUrl ?: "")
                    }

                }.cancelled {
                    LoginReplyPacket(cancelReason).write(osuWriter)
                }
            } else {
                LoginReplyPacket(BanchoLoginCancelReason.AUTHENTICATION_FAILED).write(osuWriter)
            }

            return
        }

        val token = UUID.fromString(request.headers["osu-token"]!!)
        if (token !in Hirasawa.banchoUsers) {
            // At some point their token has been invalidated or for some reason they're connecting using a token we
            // haven't seen before, we'll tell them to reconnect to authenticate with us now

            BanchoRestartPacket(10).write(osuWriter)
            return
        }

        val user = Hirasawa.banchoUsers[token]!!
        user.updateKeepAlive()

        while (request.inputStream.available() > 1) {
            val id = osuReader.readShort()
            osuReader.skipBytes(1) // unused byte
            val payloadLength = osuReader.readInt()
            val payload = request.inputStream.readNBytes(payloadLength)

            val packetReader = OsuReader(ByteArrayInputStream(payload))
            val banchoPacketType = BanchoPacketType.fromId(id)

            if (banchoPacketType == BanchoPacketType.UNKNOWN) continue

            Hirasawa.packetRouter[banchoPacketType]?.handle(packetReader, osuWriter, user)
        }


        for (packet in user.packetCache) {
            packet.write(osuWriter)
        }
        user.packetCache.clear()

    }

}