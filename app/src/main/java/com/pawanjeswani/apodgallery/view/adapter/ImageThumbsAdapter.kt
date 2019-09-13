package com.pawanjeswani.apodgallery.view.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pawanjeswani.apodgallery.R
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.view.activity.ImageActivity


class ImageThumbsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    companion object{
        var IMG_DATA  ="img_data"
        var CURRENT_IMAGE  ="current_image"
        var IMG_DATA_BUNDLE  ="img_data_bundle"
    }
    private var imgList = mutableListOf<ImageData>()
    var mContext: Context? = null


    constructor(context: Context) {
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false);
        return SentMessageHolder(view)
    }

    fun setImages(imageList: ArrayList<ImageData>) {
        imgList.clear()
        this.imgList.addAll(imageList)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return imgList.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var holder = holder as SentMessageHolder
        holder.bind(imgList[position])
        holder.itemView.setOnClickListener {
            var intent = Intent(mContext,ImageActivity::class.java)
            var bundle = Bundle()
            bundle.putSerializable(IMG_DATA,imgList[position])
            intent.putExtra(IMG_DATA_BUNDLE,bundle)
            mContext!!.startActivity(intent)
        }
    }

    private open inner class ReceivedMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        internal var iv_thumb: ImageView = itemView.findViewById(R.id.iv_thumb)

        internal fun bind(imgData: ImageData) {
            Glide.with(mContext!!)
                .load(imgData.url)
                .apply(RequestOptions().override(400,400).centerCrop())
                .into(iv_thumb)
        }
    }

    private inner class SentMessageHolder(itemView: View) : ReceivedMessageHolder(itemView)
}