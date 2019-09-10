package com.pawanjeswani.apodgallery

import android.app.Application
import com.pawanjeswani.apodgallery.service.database.DatabaseCreator
import com.pawanjeswani.apodgallery.service.datarepo.DataRepository

class ApodGalleryApp : Application() {


    override fun onCreate() {

        super.onCreate()
        app = this
//        SharedPrefUtil.set(this)
        DataRepository.instance()
        DatabaseCreator.getInstance()!!.createDb(this)
    }

    companion object {

        private var app: ApodGalleryApp? = null

        fun instance(): ApodGalleryApp? {
            return app
        }
    }
}
