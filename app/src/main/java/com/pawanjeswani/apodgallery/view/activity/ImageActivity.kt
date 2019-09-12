package com.pawanjeswani.apodgallery.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.view.adapter.ImageThumbsAdapter.Companion.IMG_DATA
import com.pawanjeswani.apodgallery.view.adapter.ImageThumbsAdapter.Companion.IMG_DATA_BUNDLE
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {

    lateinit var imgData: ImageData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        var bundle = intent.getBundleExtra(IMG_DATA_BUNDLE)
        imgData = bundle.getSerializable(IMG_DATA) as ImageData

        Glide.with(this)
            .load(imgData.hdurl)
            .into(iv_full_screen)
    }
}
