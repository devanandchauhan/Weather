package com.devanand.weather.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

object Constants {

    const val APP_ID:String ="a464461a9e22effe62a3c44c1e67979c"
    const val BASE_URL:String ="https://api.openweathermap.org/data/"
    const val METRIC_UNIT:String ="metric"
    const val PREFERENCE_NAME = "WeatherAppPreference"
    const val WEATHER_RESPONSE_DATA = "weather_response_data"
    const val TAG_MAINACTIVITY = "MainActivity"
    const val TAG_WEATHEARACTIVITY ="WeatherActivity"
    const val TAG_LOGINACTIVITY ="LoginActivity"

    fun isNetworkAvailable(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val activityNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when{
                activityNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activityNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
                activityNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true

                else -> false
            }
        }else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }
}