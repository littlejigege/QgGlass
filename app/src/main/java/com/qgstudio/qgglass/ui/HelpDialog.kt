package com.qgstudio.qgglass.ui

import android.app.DialogFragment
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.maps.model.LatLng
import com.mobile.utils.showToast
import com.qgstudio.qgglass.R
import kotlinx.android.synthetic.main.dialog_help.view.*

class HelpDialog : DialogFragment() {
    private var intent: Intent? = null

    companion object {
        fun withLatlng(latLng: LatLng): HelpDialog {
            val helpDialog = HelpDialog()
            helpDialog.buildIntent(latLng)
            return helpDialog
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_help, container, false)
        view.btnToMapApp.setOnClickListener {
            try {
                intent?.let {
                    activity.startActivity(intent)
                }
            } catch (e: ActivityNotFoundException) {
                showToast("使用此功能请安装高德地图")
            }
        }
        return view
    }

    private fun buildIntent(latLng: LatLng) {
        val dataBuilder = StringBuilder("amapuri://route/plan/?sid=BGVIS1")
        dataBuilder.append("&sourceApplication=").append("QgGlass")
                .append("&dlat=").append(latLng.latitude)
                .append("&dlon=").append(latLng.longitude)
                .append("&dev=").append(1)
                .append("&t=").append(0)
        this.intent = Intent("android.intent.action.VIEW", Uri.parse(dataBuilder.toString()))
    }

    override fun onResume() {
        val params = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        super.onResume()
    }
}