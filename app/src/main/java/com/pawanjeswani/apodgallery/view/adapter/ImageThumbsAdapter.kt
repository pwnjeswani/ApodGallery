package com.pawanjeswani.apodgallery.view.adapter

import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.util.Constans.Companion.IMG_DATE
import com.pawanjeswani.apodgallery.view.activity.ImageActivity
import com.pawanjeswani.apodgallery.view.activity.MainActivity


class ImageThumbsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private var VIEW_TYPE_ITEM = 1
    private var VIEW_TYPE_LOADING = 0
    private var imgList = mutableListOf<ImageData?>()
    var mContext: Context? = null
    var width = 0


    constructor(context: Context) {
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val displayMetrics = DisplayMetrics()
        (mContext as MainActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        return if (viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
            ImageViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.loading_item, parent, false)
            LoadingViewHodler(view)
        }
    }

    fun updateImagesList(imageList: ArrayList<ImageData?>) {
        imgList.clear()
        this.imgList.addAll(imageList)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (imgList[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return imgList.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_ITEM -> {
                var holderr = holder as ImageViewHolder
                holderr.bind(imgList[position]!!)
                holderr.itemView.setOnClickListener {
                    var intent = Intent(mContext, ImageActivity::class.java)
                    intent.putExtra(IMG_DATE, imgList[position]!!.date)
                    mContext!!.startActivity(intent)
                }
            }
            VIEW_TYPE_LOADING -> {

            }
        }
    }

    fun addToList() {
        imgList.add(null)
        notifyItemInserted(imgList.size - 1)
    }

    fun removedLoad() {
        if (imgList.isNotEmpty() && imgList[imgList.size - 1] == null) {
            imgList.removeAt(imgList.size - 1)
            notifyItemRemoved(imgList.size - 1)
        }
    }

    private inner class ImageViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        internal var iv_thumb: ImageView = itemView.findViewById(R.id.iv_thumb)

        internal fun bind(imgData: ImageData) {
            Glide.with(mContext!!)
                .applyDefaultRequestOptions(
                    RequestOptions().error(mContext!!.resources.getDrawable(R.drawable.placeholder)).placeholder(
                        mContext!!.resources.getDrawable(R.drawable.placeholder)
                    )
                )
                .load(imgData.url)
                .apply(RequestOptions().override(width / 3, width / 3).centerCrop())
                .into(iv_thumb)
        }
    }

    private inner class LoadingViewHodler internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        internal var progress_bar: ProgressBar = itemView.findViewById(R.id.progress_bar_loading)
    }

}