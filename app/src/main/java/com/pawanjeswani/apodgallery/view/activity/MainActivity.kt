package com.pawanjeswani.apodgallery.view.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.model.ApodRequest
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.util.Constans.Companion.PageSize
import com.pawanjeswani.apodgallery.util.Constans.Companion.REQUEST_CODE_INTERNET
import com.pawanjeswani.apodgallery.util.GeneralUtils
import com.pawanjeswani.apodgallery.util.GeneralUtils.getDaysAgo
import com.pawanjeswani.apodgallery.util.GeneralUtils.getDaysBefore
import com.pawanjeswani.apodgallery.util.GeneralUtils.getStartDateFor
import com.pawanjeswani.apodgallery.util.GeneralUtils.todayDate
import com.pawanjeswani.apodgallery.util.NetworkUtil
import com.pawanjeswani.apodgallery.view.adapter.ImageThumbsAdapter
import com.pawanjeswani.apodgallery.viewmodel.ApodViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var imageAdapter: ImageThumbsAdapter
    lateinit var apodViewModel: ApodViewModel
    private var loading = false
    private var listOfImges = arrayListOf<ImageData?>()
    var pageNo = 1
    var isConnect: Boolean = false
    var gotError: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init() {
        imageAdapter = ImageThumbsAdapter(this)
        apodViewModel = ViewModelProviders.of(this).get(ApodViewModel::class.java)
        isConnect = NetworkUtil.isInternetAvailable(this)
        if(!isConnect){
            showNoNetSnakBar()
        }
        fetchInitialImages()
        setUpRecyclerview()
        initScrollListener()
    }
    private fun fetchInitialImages() {
        if(apodViewModel.getDbSize()>0){
            //already sotred in DB hence not re-requesting the stored images
            loadLocalImages()
        }
        else{
            //fresh Database with no entries hence getting initial images
            var endDate = getDaysAgo(PageSize - 1)
            var apodRequest = ApodRequest()
            apodRequest.start_date = GeneralUtils.dateFormatter.format(endDate)
            if(!gotError){
                apodRequest.end_date = GeneralUtils.dateFormatter.format(todayDate)
            }
            else{
                apodRequest.end_date = GeneralUtils.dateFormatter.format(getDaysAgo(1))
            }
            if (isConnect) {
                //connected to internet hence fetching initial images from Remote Source
                apodViewModel.getRemoteImages(apodRequest).observe(this, androidx.lifecycle.Observer {
                    if (it != null && it.isNotEmpty()) {
                        var revestList = it.reversed().filter { it.media_type.equals("image") }
                        updateList(revestList,true)
                    }
                    else{
                        //got error need to handle
                        gotError = true
                        fetchInitialImages()
                    }
                })
            } else {
                //no internet loading saved images from Database
                loadLocalImages()
            }
        }
    }
    private fun setUpRecyclerview() {
        var gridLayoutManager = GridLayoutManager(this, 3)
        rv_images.layoutManager = gridLayoutManager
        rv_images.adapter = imageAdapter
    }

    private fun initScrollListener() {
        rv_images.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val gridLlm = rv_images.layoutManager as GridLayoutManager?
                if (!loading) {
                    if (gridLlm != null && gridLlm.findLastCompletelyVisibleItemPosition() == (listOfImges.size - 1)) {
                        //bottom of list hence loading more
                        pageNo++
                        loadMore()
                    }
                }
            }
        })
    }

    private fun loadLocalImages() {
        apodViewModel.getLocalImages().observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                updateList(it,false)
            }
        })
    }

    private fun updateList(revestList: List<ImageData>,neededToStore: Boolean) {
        listOfImges.addAll(revestList as ArrayList<ImageData?>)
        if (neededToStore)
            saveInDb(revestList)
        gotImages()
        imageAdapter.updateImagesList(listOfImges)
        pageNo++
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
//        Toast.makeText(this, "added $count Elements", Toast.LENGTH_LONG).show()
    }


    private fun loadMore() {
        isConnect = NetworkUtil.isInternetAvailable(this)
        if (isConnect) {
            loading = true
//            listOfImges.add(null)
            imageAdapter.addToList()
            fetchMoreImages()
        }
    }

    private fun fetchMoreImages() {
        var strinugdate = listOfImges[listOfImges.size-2]!!.date!!
        var nextEndDate =  GeneralUtils.dateFormatter.parse(strinugdate)
        nextEndDate = getDaysBefore(nextEndDate,1)
        var nextStartDate = getStartDateFor(nextEndDate)
        var apodRequest = ApodRequest()
        apodRequest.start_date = GeneralUtils.dateFormatter.format(nextStartDate)
        apodRequest.end_date = GeneralUtils.dateFormatter.format(nextEndDate)
        apodViewModel.getRemoteImages(apodRequest).observe(this, androidx.lifecycle.Observer {
            if (it != null && it.isNotEmpty()) {
                //gotImages success hence removing the loading null item from the list
//                gotImages()
                var revestList = it.reversed().filter { it.media_type.equals("image") }
                updateList(revestList,true)
            }
        })
    }

    private fun gotImages() {
        loading = false
//        listOfImges.removeAt(listOfImges.size - 1)
        imageAdapter.removedLoad()
//        imageAdapter.notifyItemRemoved(listOfImges.size - 1)
    }


    fun showNoNetSnakBar(){
        Snackbar.make(cl_parent, "You're offline ", Snackbar.LENGTH_SHORT)
            .setAction("Turn on your internet", View.OnClickListener {
                startActivityForResult(Intent(Settings.ACTION_WIFI_SETTINGS),REQUEST_CODE_INTERNET)

            })
            .setDuration(BaseTransientBottomBar.LENGTH_INDEFINITE)
            .show()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        imageAdapter.notifyDataSetChanged()
        super.onConfigurationChanged(newConfig)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        isConnect = NetworkUtil.isInternetAvailable(this)
        if(!isConnect){
            showNoNetSnakBar()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
