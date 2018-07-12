package com.qgstudio.qgglass.data

import com.amap.api.maps.model.LatLng

data class ServerInfo(val latitude: Double,
                      val longitude: Double,
                      val info: String) {
    fun getLatLng() = LatLng(latitude, longitude)

}
