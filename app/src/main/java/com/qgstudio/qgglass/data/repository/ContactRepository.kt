package com.qgstudio.qgglass.data.repository

import com.qgstudio.qgglass.data.User

object ContactRepository : BaseRepository<User> {
    override fun findAll(): List<User> {
return mutableListOf()
    }

    override fun delete(t: User) {

    }

    override fun add(t: User) {

    }

    override fun update(t: User) {

    }

}