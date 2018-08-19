package com.qgstudio.qgglass.net.api

import com.qgstudio.qgglass.data.ResultV2
import com.qgstudio.qgglass.data.User
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UsrApi {
    @POST("login/check")
    fun login(@Body user: User): Observable<ResultV2<String>>

    @POST("login/request")
    fun getCode(@Body user: User): Observable<ResultV2<String>>

    @GET("login/getwarning")
    fun getWarning(): Observable<ResultV2<String>>
}