package com.pawanjeswani.apodgallery.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.model.ApodRequest
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.util.NetworkUtil
import com.pawanjeswani.apodgallery.view.adapter.ImageThumbsAdapter
import com.pawanjeswani.apodgallery.viewmodel.ApodViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var imageAdapter: ImageThumbsAdapter
    lateinit var apodViewModel: ApodViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageAdapter = ImageThumbsAdapter(this)
        apodViewModel = ViewModelProviders.of(this).get(ApodViewModel::class.java)
        var isConnect = NetworkUtil.isInternetAvailable(this)
        setUpRecyclerview()
        fetchImageList()
    }

    private fun fetchImageList() {
        var apodRequest = ApodRequest()
        var df = SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH)
        var todayDate = Date()
        todayDate.time = System.currentTimeMillis()
        var twentyDaysBefore = getDaysAgo(20)
        apodRequest.start_date = df.format(twentyDaysBefore)
        apodRequest.end_date = df.format(todayDate)
        apodViewModel.getRemoteImages(apodRequest).observe(this, androidx.lifecycle.Observer {
            if (it != null && it.isNotEmpty()) {
                var revestList = it.reversed().filter { it.media_type.equals("image") }
                saveInDb(revestList)
                imageAdapter.setImages(revestList as ArrayList<ImageData>)
            }
        })
    }

    private fun saveInDb(imgList: List<ImageData>?) {
        var count = 0
        var mutableList: MutableList<ImageData> = imgList as MutableList<ImageData>
        for (i in 0 until mutableList.size) {
            var image = mutableList[i]
            image.image_id = image.date!!
            mutableList[i] = image
            count++
//            apodViewModel.saveImage(img, DbQueryListener {
//                count++
//            })
        }
        apodViewModel.storeMultipleImages(mutableList)
        Toast.makeText(this, "added $count Elements", Toast.LENGTH_LONG).show()
    }


    private fun setUpRecyclerview() {
        var gridLayoutManager = GridLayoutManager(this, 3)
        rv_images.layoutManager = gridLayoutManager
        rv_images.adapter = imageAdapter
    }

    fun getDaysAgo(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)

        return calendar.time
    }
}
