package com.pawanjeswani.apodgallery.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.util.NetworkUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var isConnect = NetworkUtil.isInternetAvailable(this)
    }
}
