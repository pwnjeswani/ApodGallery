package com.pawanjeswani.apodgallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pawanjeswani.apodgallery.model.ApodRequest
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.service.database.interfaces.DbQueryListener
import com.pawanjeswani.apodgallery.service.datarepo.DataRepository
import com.pawanjeswani.apodgallery.service.datarepo.LocalDataRepository
import java.util.*

class ApodViewModel : ViewModel() {

    private val dataRepository: DataRepository? = DataRepository.instance()
    private val localDataRepository: LocalDataRepository? = LocalDataRepository.instance()

    fun getRemoteImages(apodRequest: ApodRequest):LiveData<List<ImageData>>
    {
        val liveData = MutableLiveData<List<ImageData>>()
        with(dataRepository!!){
            getImageList(apodRequest).observeForever(liveData::setValue)
        }
        return liveData
    }

    fun saveImage(imageData: ImageData, listener: DbQueryListener)
    {
        localDataRepository!!.insertImageToDb(imageData,listener)
    }

    fun storeMultipleImages(list:List<ImageData>){
        localDataRepository!!.insetrtMultiples(list)
    }
    fun getLocalImages():LiveData<List<ImageData>>{
        val conversation = MutableLiveData<List<ImageData>>()
        localDataRepository!!.getAllLocalImages().observeForever {
                response-> conversation.value = response
        }
        return conversation
    }
    fun fetchImagesImageId(imageId:String):LiveData<ImageData>{
        val conversation = MutableLiveData<ImageData>()
        localDataRepository!!.getSpecifiedImage(imageId).observeForever {
                response-> conversation.value = response
        }
        return conversation
    }

    fun clearDatabse(){
        localDataRepository!!.clearDb(ImageData())
    }
}
