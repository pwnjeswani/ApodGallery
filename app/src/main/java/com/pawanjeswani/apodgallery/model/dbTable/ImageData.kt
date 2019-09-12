package com.pawanjeswani.apodgallery.model.dbTable

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
class ImageData :Serializable {

    @PrimaryKey
    var image_id: String = ""

    @SerializedName("copyright")
    @Expose
    var copyright: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("explanation")
    @Expose
    var explanation: String? = null

    @SerializedName("hdurl")
    @Expose
    var hdurl: String? = null

    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("media_type")
    @Expose
    var media_type: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

}