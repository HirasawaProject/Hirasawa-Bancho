package io.hirasawa.server.lookupmaps

interface ILookupMap<T> {
    operator fun get(key: String): T?
    operator fun get(id: Int): T?
}