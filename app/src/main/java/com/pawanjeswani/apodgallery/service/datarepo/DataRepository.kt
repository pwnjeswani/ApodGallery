package com.pawanjeswani.apodgallery.service.datarepo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pawanjeswani.apodgallery.BuildConfig
import com.pawanjeswani.apodgallery.model.ApodRequest
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.service.builder.RetrofitBuilder
import com.pawanjeswani.apodgallery.service.network.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataRepository() {

    private val dataService: APIService = RetrofitBuilder.instance!!.create(APIService::class.java)

    fun getImageList(apodRequest: ApodRequest): LiveData<List<ImageData>> {
        apodRequest.api_key = BuildConfig.API_KEY
        val data = MutableLiveData<List<ImageData>>()
        val call = dataService.getImageData(apodRequest.api_key.toString(),apodRequest.start_date.toString(), apodRequest.end_date.toString())
        call.enqueue(object : Callback<List<ImageData>> {
            override fun onResponse(call: Call<List<ImageData>>, response: Response<List<ImageData>>) {
                data.value = response.body()
            }

            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                data.value = null
            }
        })
        return data
    }

    fun getSingleImage(apodRequest: ApodRequest): LiveData<ImageData> {
        apodRequest.api_key = BuildConfig.API_KEY
        val data = MutableLiveData<ImageData>()
        val call = dataService.getSingleImageData(apodRequest.api_key.toString(),apodRequest.start_date.toString())
        call.enqueue(object : Callback<ImageData> {
            override fun onResponse(call: Call<ImageData>, response: Response<ImageData>) {
                data.value = response.body()
            }

            override fun onFailure(call: Call<ImageData>, t: Throwable) {
                data.value = null
            }
        })
        return data
    }

    companion object {
        private var dataRepository: DataRepository? = null

        fun instance(): DataRepository? {
            if (dataRepository == null) {
                dataRepository = DataRepository()
            }
            return dataRepository
        }
    }
}