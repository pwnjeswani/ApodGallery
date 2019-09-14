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
import com.pawanjeswani.apodgallery.util.GeneralUtils
import com.pawanjeswani.apodgallery.util.GeneralUtils.todayDate
import com.pawanjeswani.apodgallery.util.NetworkUtil
import com.pawanjeswani.apodgallery.util.ViewPagerPaginate
import com.pawanjeswani.apodgallery.view.fragment.SingleImageFragment
import com.pawanjeswani.apodgallery.viewmodel.ApodViewModel
import kotlinx.android.synthetic.main.activity_image.*


class ImageActivity : AppCompatActivity(), ViewPagerPaginate.ViewPagerCallBacks,
    ViewPagerPaginate.Callbacks {

    var selectedImgPosition = 0
    private var selectedImgDate = ""
    private var hasMore = true
    var page = 1
    private var loading = false
    private var currentImagePosition = 0
    private var lastPosition = 0
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
                    //adding the respective date's fragment
//                    listOfImges.add(it)
//                    addSelectedFragment(it)
                    //checking if next and prev available in DB
                    checkForOtherImages()
                }
            })
        } else {
            //offline hence getting image data from DB
            apodViewModel.fetchImagesImageId(selectedImgDate)
                .observe(this, androidx.lifecycle.Observer {
                    if (it != null) {
                        listOfImges.add(it)
                        addSelectedFragment(it)
                        checkForOtherImages()
                    }
                })
        }
    }

    private fun checkForOtherImages() {
        insertPreviousDatesImgs(isItToday = selectedImgDate== GeneralUtils.dateFormatter.format(todayDate))
    }

    private fun insertPreviousDatesImgs(isItToday:Boolean) {
        apodViewModel.getPrevImages(selectedImgDate).observe(this,androidx.lifecycle.Observer {
            if(it!=null && it.isNotEmpty()){
                for(i in 0 until it.size-1){
                    listOfImges.add(it[i])
                }
            }
            if(!isItToday){
                insertNextDatesImgs()
            }
            else{
                addFragments()
            }
        })
    }

    private fun insertNextDatesImgs() {
        apodViewModel.getNextImages(selectedImgDate).observe(this,androidx.lifecycle.Observer {
            if(it!=null && it.isNotEmpty()){
                for(i in 0 until it.size-1){
                    listOfImges.add(0,it[i])
                }
            }
            addFragments()
        })
    }

    private fun setUpViewPager() {
        viewPagerAdapter = ImageFragmentAdapter(supportFragmentManager)
        vp_images.adapter = viewPagerAdapter
        ViewPagerPaginate(vp_images, this, this)
    }


    private fun addFragments() {
//        listOfImges.add(0, listOfImges[listOfImges.size-1])
//        listOfImges.removeAt(listOfImges.size-1)
//        listOfImges.removeAt(0)
        for(img in listOfImges) {
            var imageFrag = SingleImageFragment()
            var bundle = Bundle()
            bundle.putSerializable(IMG_DATA, img)
            imageFrag.arguments = bundle
            mFragmentList.add(imageFrag)
        }
        viewPagerAdapter!!.notifyDataSetChanged()
        vp_images.currentItem = selectedImgPosition
    }

    private fun addSelectedFragment(selectedImgData:ImageData) {
        var imageFrag = SingleImageFragment()
        var bundle = Bundle()
        bundle.putSerializable(IMG_DATA, selectedImgData)
        imageFrag.arguments = bundle
        mFragmentList.add(imageFrag)
        viewPagerAdapter!!.notifyDataSetChanged()
        vp_images.currentItem = selectedImgPosition
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    inner class ImageFragmentAdapter(fm: androidx.fragment.app.FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

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

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
        }
    }

    override fun onLoadMore() {
        //it will be called when the selected image is 2 images after the current selected
        Toast.makeText(this, "onLoadMore is called", Toast.LENGTH_LONG).show()
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
        if (lastPosition > position) {
            Toast.makeText(this, "R to L called", Toast.LENGTH_LONG).show()
        }else if (lastPosition < position) {
            Toast.makeText(this, "L to R called", Toast.LENGTH_LONG).show()
        }
        lastPosition = position;
        currentImagePosition = position
        selectedImgDate = listOfImges[currentImagePosition]!!.image_id
        setImageDate()
    }
}
