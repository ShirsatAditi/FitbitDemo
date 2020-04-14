package com.example.fitbitdemo.lib

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

/**
 * Constants
 *
 * @author Aditi Shirsat
 */
object Constants {
    const val CLIENT_ID = "22BNZ7"
    const val CLIENT_SECRET = "3aa8b27400f2c2d109f44dd68ea2a888"
    const val REDIRECT_URL = "https://finished"
    const val SECURE_KEY = "aditi@12345"
    const val GRANT_TYPE = "authorization_code"

    /**
     * Check if device is connected to an active network.
     *
     * @return True if device is connected to an active network
     */
    fun isNetworkAvailable(mContext:Context): Boolean {
        val connectivityManager: ConnectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.apply {
                return getNetworkCapabilities(activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                } ?: false
            }
        } else {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

}