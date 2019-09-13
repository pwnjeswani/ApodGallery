package com.pawanjeswani.apodgallery.view.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.view.adapter.ImageThumbsAdapter
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.fragment_single_image.*

/**
 * A simple [Fragment] subclass.
 */
class SingleImageFragment : Fragment() {

    lateinit var imgData: ImageData
    var bundle: Bundle?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        bundle = arguments
        return inflater.inflate(R.layout.fragment_single_image, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgData = bundle!!.getSerializable(ImageThumbsAdapter.IMG_DATA) as ImageData

        Glide.with(this)
            .load(imgData.hdurl)
            .into(iv_full_screen)
    }



}