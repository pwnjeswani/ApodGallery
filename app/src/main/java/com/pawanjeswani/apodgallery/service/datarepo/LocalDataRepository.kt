package com.pawanjeswani.apodgallery.service.datarepo

import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.LiveData
import com.pawanjeswani.apodgallery.model.dbTable.ImageData
import com.pawanjeswani.apodgallery.service.database.AppDatabase
import com.pawanjeswani.apodgallery.service.database.DatabaseCreator
import com.pawanjeswani.apodgallery.service.database.interfaces.DbQueryListener


class LocalDataRepository {
    private var appDatabase: AppDatabase? = null

    private constructor() {
        appDatabase = DatabaseCreator.getInstance()!!.database
    }

    fun insertImageToDb(item: ImageData, listener: DbQueryListener) {
        val mHandlerThread = HandlerThread("Handler")
        mHandlerThread.start()
        val handler = Handler(mHandlerThread.looper)
        val runnable = {
            appDatabase!!.apodDao.insertImagedataToDb(item)
            listener.onSuccess()
        }
        handler.post(runnable)
    }


    fun clearDb(imageData: ImageData) {
        appDatabase!!.apodDao.deleteWholeDb(imageData)
    }

    fun getAllLocalImages(): LiveData<List<ImageData>> {
        return appDatabase!!.apodDao.getAllImages()
    }

    fun getSpecifiedImage(image_id: String): LiveData<ImageData> {
        return appDatabase!!.apodDao.getImageById(image_id)
    }

    companion object {
        private var localDataRepository: LocalDataRepository? = null

        fun instance(): LocalDataRepository? {
            if (localDataRepository == null) {
                localDataRepository = LocalDataRepository()
            }
            return localDataRepository
        }
    }
}