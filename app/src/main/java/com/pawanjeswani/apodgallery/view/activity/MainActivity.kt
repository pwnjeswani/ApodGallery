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
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var imageAdapter: ImageThumbsAdapter
    lateinit var apodViewModel: ApodViewModel
    private var loading = false
    private var listOfImges = arrayListOf<ImageData?>()
    var df = SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH)
    var pageNo = 1
    var pageSize = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageAdapter = ImageThumbsAdapter(this)
        apodViewModel = ViewModelProviders.of(this).get(ApodViewModel::class.java)
        var isConnect = NetworkUtil.isInternetAvailable(this)
        setUpRecyclerview()
        fetchImageList()
        initScrollListener()
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
                        loading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        listOfImges.add(null)
        imageAdapter.addImages(listOfImges)
        imageAdapter.notifyItemInserted(listOfImges.size-1)
        fetchMoreImages()
    }

    private fun fetchMoreImages() {
        var newOldDate = getDaysAgo((10*pageNo) -1 )
        var newEndDate = getEndDate(newOldDate)
        var apodRequest = ApodRequest()
        apodRequest.start_date = df.format(newOldDate)
        apodRequest.end_date = df.format(newEndDate)
        apodViewModel.getRemoteImages(apodRequest).observe(this, androidx.lifecycle.Observer {
            if (it != null && it.isNotEmpty()) {
                loading = false
                listOfImges.removeAt(listOfImges.size-1)
                imageAdapter.addImages(listOfImges)
                imageAdapter.notifyItemRemoved(listOfImges.size-1)

                var revestList = it.reversed().filter { it.media_type.equals("image") }
                listOfImges.addAll(revestList)
                pageNo++
                imageAdapter.addImages(listOfImges)
            }
        })

    }

    private fun fetchImageList() {
        var todayDate = Date()
        todayDate.time = System.currentTimeMillis()
        var endDate = getDaysAgo(pageSize-1)
        var apodRequest = ApodRequest()
        apodRequest.start_date = df.format(endDate)
        apodRequest.end_date = df.format(todayDate)
        apodViewModel.getRemoteImages(apodRequest).observe(this, androidx.lifecycle.Observer {
            if (it != null && it.isNotEmpty()) {
                var revestList = it.reversed().filter { it.media_type.equals("image") }
                listOfImges = revestList as ArrayList<ImageData?>
//                saveInDb(revestList)
                imageAdapter.addImages(listOfImges)
                pageNo++
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
//            apodViewModel.saveImage(img, DbQueryListener {x6
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


    fun getDaysAgo(daysBefore:Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore)
        return calendar.time
    }

    fun getEndDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, pageSize-1)
        return calendar.time
    }

}
