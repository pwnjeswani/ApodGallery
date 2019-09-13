package com.pawanjeswani.apodgallery.view.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.model.ApodRequest
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.util.Constans.Companion.IMG_DATA
import com.pawanjeswani.apodgallery.util.Constans.Companion.IMG_DATE
import com.pawanjeswani.apodgallery.util.Constans.Companion.PageSize
import com.pawanjeswani.apodgallery.util.GeneralUtils
import com.pawanjeswani.apodgallery.util.GeneralUtils.getDaysAgo
import com.pawanjeswani.apodgallery.util.GeneralUtils.todayDate
import com.pawanjeswani.apodgallery.util.NetworkUtil
import com.pawanjeswani.apodgallery.util.ViewPagerPaginate
import com.pawanjeswani.apodgallery.view.fragment.SingleImageFragment
import com.pawanjeswani.apodgallery.viewmodel.ApodViewModel
import kotlinx.android.synthetic.main.activity_image.*
import java.util.*


class ImageActivity : AppCompatActivity(), ViewPagerPaginate.ViewPagerCallBacks,
    ViewPagerPaginate.Callbacks {

    var selectedImgPosition = 0
    private var selectedImgDate = ""
    private var hasMore = true
    var page = 1
    private var loading = false
    private var currentImagePosition = 0
    lateinit var apodViewModel: ApodViewModel
    lateinit var toolbar: ActionBar
    private var listOfImges = arrayListOf<ImageData?>()
    private var viewPagerAdapter: ImageFragmentAdapter? = null
    var mFragmentList = arrayListOf<SingleImageFragment>()
    var isConnect: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.toolbar_app_theme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        apodViewModel = ViewModelProviders.of(this).get(ApodViewModel::class.java)
        isConnect = NetworkUtil.isInternetAvailable(this)
        getIntentData()
        setImageDate()
        //get which item was clicked
        /*var bundle = intent.getBundleExtra(IMG_DATA_BUNDLE)
        selectedImgPosition = bundle!!.getInt(CURRENT_IMAGE)*/

        //fetching list
        /*fetchImageList()*/

        fetchImage()

        //setting ViewPager
        setUpViewPager()
    }

    private fun getIntentData() {
        if (intent.hasExtra(IMG_DATE)) {
            selectedImgDate = intent.getStringExtra(IMG_DATE)
        }
    }

    private fun setImageDate() {
        toolbar = supportActionBar!!
        toolbar.setDisplayHomeAsUpEnabled(true)
        toolbar.title = selectedImgDate
    }

    private fun fetchImage() {
        var apodRequest = ApodRequest()
        apodRequest.start_date = selectedImgDate
//        apodViewModel.getPrevImages(selectedImgDate).observe(this,androidx.lifecycle.Observer {
//            if (it != null && it.isNotEmpty()) {
//                Toast.makeText(this, "prev images list size is ${it.size}", Toast.LENGTH_LONG).show()
//            }
//        })
        if (isConnect) {
            //connectd to network hence fetching data from api service
            apodViewModel.getRemoteImage(apodRequest).observe(this, androidx.lifecycle.Observer {
                //            if (it != null && it.isNotEmpty()) {
//                var revestList = it.reversed().filter { it.media_type.equals("image") }
//                listOfImges = revestList as ArrayList<ImageData?>
//                addFragments()
//            }
//        })
                if (it != null) {
                    listOfImges.add(it)
                    addFragments()
                }
            })
        } else {
            //offline hence getting image data from DB
            apodViewModel.fetchImagesImageId(selectedImgDate)
                .observe(this, androidx.lifecycle.Observer {
                    if (it != null) {
                        listOfImges.add(it)
                        addFragments()
                    }
                })
        }
    }

    private fun setUpViewPager() {
        viewPagerAdapter = ImageFragmentAdapter(supportFragmentManager)
        vp_images.adapter = viewPagerAdapter
        ViewPagerPaginate(vp_images, this, this)
    }

    private fun fetchImageList() {
        var endDate = getDaysAgo(PageSize - 1)
        var apodRequest = ApodRequest()
        apodRequest.start_date = GeneralUtils.dateFormatter.format(endDate)
        apodRequest.end_date = GeneralUtils.dateFormatter.format(todayDate)
        apodViewModel.getRemoteImages(apodRequest).observe(this, androidx.lifecycle.Observer {
            if (it != null && it.isNotEmpty()) {
                var revestList = it.reversed().filter { it.media_type.equals("image") }
                listOfImges = revestList as ArrayList<ImageData?>
                addFragments()
            }
        })
    }

    private fun addFragments() {
        for (img in listOfImges) {
            var imageFrag = SingleImageFragment()
            var bundle = Bundle()
            bundle.putSerializable(IMG_DATA, img)
            imageFrag.arguments = bundle
            mFragmentList.add(imageFrag)
        }
        viewPagerAdapter!!.notifyDataSetChanged()
        vp_images.currentItem = selectedImgPosition
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    inner class ImageFragmentAdapter(fm: androidx.fragment.app.FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }

        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            var fragment = androidx.fragment.app.Fragment()
            if (mFragmentList.size >= position) {
                fragment = mFragmentList.get(position)
            }
            return fragment
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }
    }

    override fun onLoadMore() {
//        Toast.makeText(this, "onLoadMore is called", Toast.LENGTH_LONG).show()
    }

    override fun isLoading(): Boolean = loading

    override fun hasLoadedAllItems(): Boolean = false

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        var newPosition = position
        var oldPosition = newPosition - 1

    }

    override fun onPageSelected(position: Int) {
        currentImagePosition = position
    }
}
