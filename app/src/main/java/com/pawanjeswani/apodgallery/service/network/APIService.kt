package com.pawanjeswani.apodgallery.service.network


import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    @GET("apod")
    fun getImageData(@Query("api_key") api_key:String,
                    @Query("start_date") start_date:String,
                    @Query("end_date") end_date:String
                    ): Call<List<ImageData>>

    @GET("apod")
    fun getSingleImageData(@Query("api_key") api_key:String, @Query("date") date:String): Call<ImageData>

}