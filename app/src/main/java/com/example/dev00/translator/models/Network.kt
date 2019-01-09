package com.example.dev00.translator.models

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


class Network {

    companion object {
        var isConnected: String = ""

        fun isConnectingToInternet(_context: Context): Boolean {
            if (!isWiFiConnected(_context)){
                if (!isOtherNetworkConnected(_context)){
                    return false
                }
            }
            return true
        }

        private fun isWiFiConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            } else {
                connectivityManager.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
            }
        }

        private fun isOtherNetworkConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return  return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            } else {
                connectivityManager.activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE
            }
        }
    }
}