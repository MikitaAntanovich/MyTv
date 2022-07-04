package com.horizont.mytv

import android.Manifest
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import androidx.core.app.ActivityCompat


class NetworkManager {
    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        }

        fun getMacAddress(context: Context): String {
            val wifiManager =
                context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager?
            val wifiInfo = wifiManager!!.connectionInfo
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return "empty"
            }
            return wifiInfo.bssid
        }

        fun getWifiName(context: Context): String {
            val wifiManager =
                context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager?
            val wifiInfo = wifiManager!!.connectionInfo
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return "impossible"
            }
            return wifiInfo.ssid
        }
    }
}

