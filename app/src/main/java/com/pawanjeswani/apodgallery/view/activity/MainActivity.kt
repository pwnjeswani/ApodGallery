package com.pawanjeswani.apodgallery.view.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
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
import androidx.recyclerview.widget.RecyclerView
import com.pawanjeswani.apodgallery.util.Constans.Companion.PageSize
import com.pawanjeswani.apodgallery.util.GeneralUtils
import com.pawanjeswani.apodgallery.util.GeneralUtils.getDaysAgo
import com.pawanjeswani.apodgallery.util.GeneralUtils.getEndDate
import com.pawanjeswani.apodgallery.util.GeneralUtils.todayDate
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var imageAdapter: ImageThumbsAdapter
    lateinit var apodViewModel: ApodViewModel
    private var loading = false
    private var listOfImges = arrayListOf<ImageData?>()
    var pageNo = 1
    var isConnect:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageAdapter = ImageThumbsAdapter(this)
        apodViewModel = ViewModelProviders.of(this).get(ApodViewModel::class.java)
        isConnect = NetworkUtil.isInternetAvailable(this)
        setUpRecyclerview()
        fetchInitialImages()
        initScrollListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        imageAdapter.notifyDataSetChanged()
        super.onConfigurationChanged(newConfig)
    }
    private fun setUpRecyclerview() {
        var gridLayoutManager = GridLayoutManager(this, 3)
        rv_images.layoutManager = gridLayoutManager
        rv_images.adapter = imageAdapter
    }

    private fun fetchInitialImages() {
        var endDate = getDaysAgo(PageSize-1)
        var apodRequest = ApodRequest()
        apodRequest.start_date = GeneralUtils.dateFormatter.format(endDate)
        apodRequest.end_date = GeneralUtils.dateFormatter.format(todayDate)
        if(isConnect){
            //fetching images from Remote Source
            apodViewModel.getRemoteImages(apodRequest).observe(this, androidx.lifecycle.Observer {
                if (it != null && it.isNotEmpty()) {
                    var revestList = it.reversed().filter { it.media_type.equals("image") }
                    updateList(revestList)
                }
            })
        }
        else{
            //loading saved images from Database
            apodViewModel.getLocalImages().observe(this, androidx.lifecycle.Observer {
                if (it != null && it.isNotEmpty()) {
//                    var revestList = it.reversed().filter { it.media_type.equals("image") }
                    updateList(it)
                }
            })
        }

    }

    private fun updateList(revestList: List<ImageData>) {
        listOfImges.addAll(revestList  as ArrayList<ImageData?>)
        if(isConnect)
            saveInDb(revestList)
        imageAdapter.updateImagesList(listOfImges)
        pageNo++
    }

    private fun initScrollListener() {
        rv_images.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val gridLlm = rv_images.layoutManager as GridLayoutManager?
                if (!loading) {
                    if (gridLlm != null && gridLlm.findLastCompletelyVisibleItemPosition() == (listOfImges.size-1)) {
                        //bottom of list hence loading more
                        loadMore()
                    }
                }
            }
        })
    }

    private fun loadMore() {
        loading = true
        listOfImges.add(null)
        imageAdapter.updateImagesList(listOfImges)
        imageAdapter.notifyItemInserted(listOfImges.size-1)
        fetchMoreImages()
    }

    private fun fetchMoreImages() {
        var newOldDate = getDaysAgo((10*pageNo) -1 )
        var newEndDate = getEndDate(newOldDate)
        var apodRequest = ApodRequest()
        apodRequest.start_date = GeneralUtils.dateFormatter.format(newOldDate)
        apodRequest.end_date = GeneralUtils.dateFormatter.format(newEndDate)
        apodViewModel.getRemoteImages(apodRequest).observe(this, androidx.lifecycle.Observer {
            if (it != null && it.isNotEmpty()) {
                //gotImages success hence removing the loading null item from the list
                gotImages()
                var revestList = it.reversed().filter { it.media_type.equals("image") }
                updateList(revestList)
            }
        })
    }

    private fun gotImages() {
        loading = false
        listOfImges.removeAt(listOfImges.size-1)
        imageAdapter.updateImagesList(listOfImges)
        imageAdapter.notifyItemRemoved(listOfImges.size-1)
    }


    private fun saveInDb(imgList: List<ImageData>?) {
        var count = 0
        var mutableList: MutableList<ImageData> = imgList as MutableList<ImageData>
        for (i in 0 until mutableList.size) {
            var image = mutableList[i]
            image.image_id = image.date!!
            mutableList[i] = image
            count++
        }
        apodViewModel.storeMultipleImages(mutableList)
        Toast.makeText(this, "added $count Elements", Toast.LENGTH_LONG).show()
    }



}
