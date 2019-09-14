package com.pawanjeswani.apodgallery.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView
import com.pawanjeswani.apodgallery.service.datarepo.LocalDataRepository
import com.pawanjeswani.apodgallery.viewmodel.ApodViewModel

class ImageFragmentAdapter (val context: Context,val position: Int, val apodViewModel: ApodViewModel) : PagerAdapter() {

    lateinit var photView: PhotoView

    override fun getCount(): Int {

        return 9
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        var fragment = androidx.fragment.app.Fragment()
//        if (mFragmentList.size >= position) {
//            fragment = mFragmentList.get(position)
//        }
        return fragment
        return super.instantiateItem(container, position)
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}