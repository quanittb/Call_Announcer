package com.mobiai.app.ultils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


class NetWorkChecker  {

    companion object{
        val instance: NetWorkChecker by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetWorkChecker()
        }
    }
    fun isNetworkConnected(context: Context) : Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities =
            connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

}