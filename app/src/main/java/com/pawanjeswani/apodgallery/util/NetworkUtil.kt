package com.pawanjeswani.apodgallery.util

import android.content.Context
import android.net.ConnectivityManager
import com.pawanjeswani.apodgallery.apodgallery


object NetworkUtil {

    fun isNetworkConnected(): Boolean
        {
            val cm = apodgallery.instance()!!
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm?.activeNetworkInfo != null
        }
}
