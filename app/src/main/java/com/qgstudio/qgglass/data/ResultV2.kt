package com.qgstudio.qgglass.data

data class ResultV2<out T>(val state:Int, val info:String, val data:T) {
}