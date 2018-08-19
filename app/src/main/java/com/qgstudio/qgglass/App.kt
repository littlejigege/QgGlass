package com.qgstudio.qgglass

import android.app.Application
import com.mobile.utils.Utils
import com.qgstudio.qgglass.net.api.ContactApi
import com.qgstudio.qgglass.net.api.UsrApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Created by jimiji on 2018/4/19.
 */
class App : Application() {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                    .client(OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }).build())
                    .baseUrl("http://39.108.110.121:8888/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        val usrApi by lazy { retrofit.create(UsrApi::class.java) }
        val contactApi by lazy { retrofit.create(ContactApi::class.java) }
    }

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }

}