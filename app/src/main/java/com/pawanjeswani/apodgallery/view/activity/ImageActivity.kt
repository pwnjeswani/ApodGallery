package com.pawanjeswani.apodgallery.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.view.adapter.ImageThumbsAdapter.Companion.CURRENT_IMAGE
import com.pawanjeswani.apodgallery.view.adapter.ImageThumbsAdapter.Companion.IMG_DATA_BUNDLE

class ImageActivity : AppCompatActivity() {

     var selectedImage = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        var bundle = intent.getBundleExtra(IMG_DATA_BUNDLE)
        selectedImage = bundle.getInt(CURRENT_IMAGE)


    }
}
