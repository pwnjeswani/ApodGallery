package com.pawanjeswani.apodgallery.view.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.util.Constans
import com.pawanjeswani.apodgallery.util.Constans.Companion.IMG_DATA
import kotlinx.android.synthetic.main.fragment_single_image.*

/**
 * A simple [Fragment] subclass.
 */
class SingleImageFragment : Fragment() {

    private lateinit var imgData: ImageData
    private var bundle: Bundle? = null
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    internal var bottomSheetSlideOffset: Float = 0.toFloat()
    internal var bottomSheetState: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bundle = arguments
        imgData = bundle!!.getSerializable(IMG_DATA) as ImageData
        return inflater.inflate(R.layout.fragment_single_image, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgData = bundle!!.getSerializable(IMG_DATA) as ImageData
        Glide.with(this)
            .load(imgData.url)
            .into(iv_full_screen)
        setDetails()
        setupBottomSheet(savedInstanceState)
        iv_full_screen.setOnClickListener {
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun setDetails() {
        tv_title.text = imgData.title
        if (!imgData.copyright.isNullOrEmpty())
            tv_copyright.text = "Copyrights - ${imgData.copyright}"
        tv_content.text = imgData.explanation
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(Constans.EXTRA_BOTTOM_SHEET_SLIDE_OFFSET, bottomSheetSlideOffset)
    }

    private fun setupBottomSheet(savedInstanceState: Bundle?) {
        bottomSheetBehavior = BottomSheetBehavior.from(ll_img_details)
        if (savedInstanceState != null)
            bottomSheetSlideOffset =
                savedInstanceState.getFloat(Constans.EXTRA_BOTTOM_SHEET_SLIDE_OFFSET, 0f)
        setScrim(bottomSheetSlideOffset)

        bottomSheetBehavior!!.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                setBottomSheetState(newState)
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN ->
                        window_dim.visibility = View.GONE
                    BottomSheetBehavior.STATE_DRAGGING, BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_HALF_EXPANDED ->
                        window_dim.visibility = View.VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                setBottomSheetSlideOffset(slideOffset)
                setScrim(slideOffset)
            }
        })

        bottomSheetBehavior!!.isHideable = true
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN

        window_dim.setOnClickListener { v ->
            bottomSheetBehavior!!.isHideable = true
            bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun setScrim(slideOffset: Float) {
        window_dim.background.alpha = (slideOffset * 100).toInt()
    }

    fun setBottomSheetState(bottomSheetState: Int) {
        if (bottomSheetState == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior!!.isHideable = true
        }
        this.bottomSheetState = bottomSheetState
    }

    fun setBottomSheetSlideOffset(bottomSheetSlideOffset: Float) {
        this.bottomSheetSlideOffset = bottomSheetSlideOffset
    }


}
