package com.qgstudio.qgglass.data.repository

interface BaseRepository<T> {
    fun findAll(): List<T>
    fun delete(t: T)
    fun add(t: T)
    fun update(t: T)
}