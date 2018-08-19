package com.qgstudio.qgglass

import android.content.Context
import android.media.MediaPlayer
import android.os.Vibrator
import android.util.Log
import com.amap.api.maps.model.LatLng
import com.mobile.utils.Preference
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.io.IOException
//简化流程
fun <T> Observable<T>.fastObserve(tag: String, onNext: (result: T) -> Unit) {
    this.subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<T> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: T) {
                    onNext(t)
                }

                override fun onError(e: Throwable) {
                    Log.e(tag, e.message)
                }
            })
}

//震动、响铃
object Bee {
    private val mp = MediaPlayer()
    private lateinit var vibrator: Vibrator
    //开始
    fun bee(ctx: Context) {
        //开始10秒的震动
        vibrator = ctx.getSystemService(android.content.Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(10000)
        //循环播放raw中的abc音频文件
        val file = ctx.resources.openRawResourceFd(R.raw.abc)
        try {
            mp.setDataSource(file.fileDescriptor, file.startOffset,
                    file.length)
            mp.prepare()
            file.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mp.isLooping = true
        mp.start()
    }
    //停止
    fun stopBee() {
        mp.stop()
        mp.reset()
        vibrator.cancel()
    }
}
//把最后接收到的坐标存入xml
fun saveLastLatlng(latLng: LatLng) {
    Preference.save("INFO") {
        "Lat" - latLng.latitude
        "Lng" - latLng.longitude
    }
}
//从xml中取出坐标
fun getLastLatLng() = LatLng(Preference.get("INFO", "Lat" to 39.906901) as Double,
        Preference.get("INFO", "Lng" to 116.397972) as Double)
