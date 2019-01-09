package com.example.dev00.translator.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.greenrobot.eventbus.EventBus
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.models.Network


class NetworkChangeReceiver: BroadcastReceiver() {

    companion object {
        var bus = EventBus.getDefault()
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val status = NetworkUtil.getConnectivityStatusString(context)
//        Toast.makeText(context, status, Toast.LENGTH_SHORT).show()
        if (status != null) {
            if (status == "Not connected to Internet") {
                Network.isConnected = Constants.NOT_CONNECTED
                bus.post(Constants.NOT_CONNECTED)
            } else {
                Network.isConnected = Constants.CONNECTED
//                Toast.makeText(context, "connected", Toast.LENGTH_SHORT).show()
                try {
                    bus.post(Network.isConnected)
                    Log.w("-------", "connect ")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}

class NetworkUtil{

    companion object {

        var TYPE_WIFI = 1
        var TYPE_MOBILE = 2
        var TYPE_NOT_CONNECTED = 0

        fun getConnectivityStatus(context: Context): Int {
            val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo
            if (null != activeNetwork) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI

                if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE
            }
            return TYPE_NOT_CONNECTED
        }

        fun getConnectivityStatusString(context: Context): String? {
            val conn = NetworkUtil.getConnectivityStatus(context)
            var status: String? = null
            if (conn == NetworkUtil.TYPE_WIFI) {
                status = "Wifi enabled"
            } else if (conn == NetworkUtil.TYPE_MOBILE) {
                status = "Mobile data enabled"
            } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
                status = "Not connected to Internet"
            }
            return status
        }
    }
}