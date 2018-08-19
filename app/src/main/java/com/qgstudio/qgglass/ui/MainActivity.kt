package com.qgstudio.qgglass.ui

import android.graphics.Color
import android.media.MediaPlayer

import android.os.Bundle
import android.util.Log
import com.amap.api.location.AMapLocationClient

import com.amap.api.maps.model.LatLng
import com.mobile.utils.permission.PermissionCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import com.mobile.utils.permission.Permission
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.CoordinateConverter
import com.mobile.utils.*
import com.qgstudio.qgglass.R
import com.qgstudio.qgglass.data.ServerInfo
import com.qgstudio.qgglass.net.WebSocketManager
import java.util.*
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.Marker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qgstudio.qgglass.data.Result
import android.os.Vibrator
import java.lang.reflect.Array.getLength
import android.content.res.AssetFileDescriptor
import com.qgstudio.qgglass.Bee
import com.qgstudio.qgglass.getLastLatLng
import com.qgstudio.qgglass.saveLastLatlng
import java.io.IOException


class MainActivity : PermissionCompatActivity() {
    private val map by lazy { mapView.map }
    private val locationClient by lazy { AMapLocationClient(applicationContext) }
    private var polyline: Polyline? = null
    companion object {
        val lList = mutableListOf<LatLng>()
    }
    private var marker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)
        fullScreen()
        setStatusBarTextBlack()

        mapView.onCreate(savedInstanceState)
        Permission.STORAGE.get(this, {})
        Permission.LOCATION.get(this, { it ->
            if (it) {
                val myLocationStyle = MyLocationStyle()
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
                myLocationStyle.showMyLocation(true)
                map.myLocationStyle = myLocationStyle//设置定位蓝点的Style
                map.uiSettings.isMyLocationButtonEnabled = true
                map.isMyLocationEnabled = true
            } else {
                showToast("未得到定位权限，无法定位")
            }
        })
        map.setOnMyLocationChangeListener {
            doAfter(500) {
                map.moveCamera(CameraUpdateFactory.zoomTo(16F))
            }
        }
        //测试代码
//        doAfter(30000) {
//            EventBus.getDefault().post(ServerInfo(23.0382722958,113.3829282020, ""))
//            EventBus.getDefault().post(ServerInfo(23.0382722958,113.3829282020, "help"))
//        }
    }

    @Subscribe
    fun onServerInfoGet(info: ServerInfo) {
        if (info.info.isEmpty()) {
            saveLastLatlng(info.getLatLng())//保存最后一个点
            addLatLngAndDraw(info.getLatLng())
        } else {
            //help指令时可能没有附带坐标信息，所以取原先列表中的最后一个点
            if (lList.isEmpty()) {
                toHelpDialog(null)
            } else {
                toHelpDialog(lList[lList.lastIndex])
            }
        }
    }

    fun toHelpDialog(latLng: LatLng?) {
        val dialog = HelpDialog.withLatlng(latLng)
        dialog.show(fragmentManager, "")
        Bee.bee(this)
    }

    fun addLatLngAndDraw(latLng: LatLng) {
        Log.e("onLocationGet", "${latLng.latitude}  ${latLng.longitude}")
        //showToast("onLocationGet:  " + "${latLng.latitude}  ${latLng.longitude}")
        val converter = CoordinateConverter(this).from(CoordinateConverter.CoordType.GPS)
        lList.add(converter.coord(latLng).convert())
        map.addPolyline(PolylineOptions().addAll(lList).width(10f).color(Color.RED))
        markLastLatLng(lList[lList.lastIndex])//标记最后一个点
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        //唤醒地图马上绘制最新的点，没有的话用new LatLng(39.906901,116.397972)替代
        markLastLatLng(getLastLatLng())
        WebSocketManager.connect("ws://39.108.110.121:8888/ws?gid=${UUID.randomUUID()}")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


    private fun markLastLatLng(latLng: LatLng) {
        if (marker == null) {
            marker = map.addMarker(MarkerOptions().position(latLng).title("老人"))
        } else {
            marker!!.position = latLng
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Bee.stopBee()
    }
}
