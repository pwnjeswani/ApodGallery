package com.pawanjeswani.apodgallery.view.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.model.ApodRequest
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.util.Constans.Companion.CURRENT_IMAGE
import com.pawanjeswani.apodgallery.util.Constans.Companion.IMG_DATA
import com.pawanjeswani.apodgallery.util.Constans.Companion.IMG_DATA_BUNDLE
import com.pawanjeswani.apodgallery.util.Constans.Companion.PageSize
import com.pawanjeswani.apodgallery.view.fragment.SingleImageFragment
import com.pawanjeswani.apodgallery.viewmodel.ApodViewModel
import kotlinx.android.synthetic.main.activity_image.*
import java.text.SimpleDateFormat
import java.util.*

class ImageActivity : AppCompatActivity() {

    var selectedImgPosition = 0
    lateinit var apodViewModel: ApodViewModel
    lateinit var toolbar: ActionBar
    private var listOfImges = arrayListOf<ImageData?>()
    private var viewPagerAdapter: ImageFragmentAdapter? = null
    var df = SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH)
    var mFragmentList = arrayListOf<SingleImageFragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.toolbar_app_theme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        apodViewModel = ViewModelProviders.of(this).get(ApodViewModel::class.java)
        toolbar = supportActionBar!!
        toolbar.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "Here Image name will appear "
        //get which item was clicked
        var bundle = intent.getBundleExtra(IMG_DATA_BUNDLE)
        selectedImgPosition = bundle!!.getInt(CURRENT_IMAGE)
        //fetching list
        fetchImageList()

        //setViewPager
        setUpViewPager()
    }

    private fun setUpViewPager() {
        viewPagerAdapter = ImageFragmentAdapter(supportFragmentManager)
        vp_images.adapter = viewPagerAdapter
    }

    private fun fetchImageList() {
        var todayDate = Date()
        todayDate.time = System.currentTimeMillis()
        var endDate = getDaysAgo(PageSize - 1)
        var apodRequest = ApodRequest()
        apodRequest.start_date = df.format(endDate)
        apodRequest.end_date = df.format(todayDate)
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

    fun getDaysAgo(daysBefore: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysBefore)
        return calendar.time
    }


    inner class ImageFragmentAdapter(fm: androidx.fragment.app.FragmentManager) :
        androidx.fragment.app.FragmentStatePagerAdapter(fm) {


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
}
