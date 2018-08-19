package com.qgstudio.qgglass.net

import android.util.Log
import com.amap.api.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobile.utils.showToast
import com.qgstudio.qgglass.data.Result
import com.qgstudio.qgglass.data.ServerInfo
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

/**
 * Created by jimiji on 2018/4/16.
 */
object WebSocketManager : WebSocketListener() {
    private var webSocket: WebSocket? = null
    private val httpClient by lazy { OkHttpClient.Builder().build() }
    //与服务器建立连接
    fun connect(url: String) {
        if (webSocket == null) {
            val request = Request.Builder().url(url).build()
            httpClient.newWebSocket(request, this)
        }
    }

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        WebSocketManager.webSocket = webSocket
        Log.e("webSocket", "onOpen")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
        Log.e("webSocket", "onFailure")
    }

    //只接受坐标json，其他信息在转换json过程中丢失抛出异常
    override fun onMessage(webSocket: WebSocket?, text: String) {
        Log.e("webSocket", "onMessage:$text")
        try {
            EventBus.getDefault().post((Gson().fromJson(text, object : TypeToken<Result<ServerInfo>>() {}.type) as Result<ServerInfo>).result)
        } catch (e: Exception) {
            Log.e("webSocketError", "onMessage:${e.message}")
        }
    }


    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        this.webSocket = null
        Log.e("webSocket", "onClosed")
    }
    //向服务器发送信息
    fun send(any: Any) {
        webSocket?.let {
            val text = Gson().toJson(any)
            it.send(text)
        }
    }
}