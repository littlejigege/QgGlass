package com.qgstudio.qgglass.net.api

import com.qgstudio.qgglass.data.ResultV2
import com.qgstudio.qgglass.data.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface ContactApi {
    @POST("contact/add")
    fun addContact(@Body user: User): Observable<ResultV2<String>>

    @POST("contact/delete")
    fun deleteContact(@Body user: User): Observable<ResultV2<String>>

    @POST("contact/show")
    fun getAllContact(): Observable<ResultV2<List<User>>>

    @POST("contact/update")
    fun updateContact(@Body user: User): Observable<ResultV2<String>>
}