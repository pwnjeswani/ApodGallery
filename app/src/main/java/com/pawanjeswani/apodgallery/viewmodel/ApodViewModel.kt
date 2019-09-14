package com.pawanjeswani.apodgallery.viewmodel

import android.util.Log
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
    fun getRemoteImage(apodRequest: ApodRequest):LiveData<ImageData>
    {
        val liveData = MutableLiveData<ImageData>()
        with(dataRepository!!){
            getSingleImage(apodRequest).observeForever(liveData::setValue)
        }
        return liveData
    }

    fun saveImage(imageData: ImageData, listener: DbQueryListener)
    {
        localDataRepository!!.insertImageToDb(imageData,listener)
    }

    fun storeMultipleImages(list:List<ImageData>){
        Log.i("Entry_in_db",list.size.toString())
        localDataRepository!!.insetrtMultiples(list)
    }
    fun getLocalImages():LiveData<List<ImageData>>{
        val conversation = MutableLiveData<List<ImageData>>()
        localDataRepository!!.getAllLocalImages().observeForever {
                response-> conversation.value = response
            Log.i("getting_local_db",conversation.value?.size.toString())
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

    fun getNextImages(imageId:String):LiveData<List<ImageData>>{
        val conversation = MutableLiveData<List<ImageData>>()
        localDataRepository!!.getNextImages(imageId).observeForever {
                response-> conversation.value = response
        }
        return conversation
    }
    fun getPrevImages(imageId:String):LiveData<List<ImageData>>{
        val conversation = MutableLiveData<List<ImageData>>()
        localDataRepository!!.getPrevImages(imageId).observeForever {
                response-> conversation.value = response
        }
        return conversation
    }

    fun getPreviousTenImages(imageId:String):LiveData<List<ImageData>>{
        val conversation = MutableLiveData<List<ImageData>>()
        localDataRepository!!.getPreviousTenImages(imageId).observeForever {
                response-> conversation.value = response
        }
        return conversation
    }

    fun getDbSize():Int = localDataRepository!!.getDbSize()

    fun clearDatabse(){
        localDataRepository!!.clearDb(ImageData())
    }
}
