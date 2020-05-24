package io.hirasawa.server.objects

data class Beatmap(val id: Int, val mapsetId: Int, val difficulty: String, val hash: String, val ranks: Int,
                   val offset: Float)