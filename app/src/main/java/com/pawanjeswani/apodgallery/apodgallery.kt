package com.pawanjeswani.apodgallery

import android.app.Application

class apodgallery : Application() {


    override fun onCreate() {

        super.onCreate()
        app = this
//        SharedPrefUtil.set(this)
//        DataRepository.instance()
//        DatabaseCreator.getInstance()!!.createDb(this)
    }

    companion object {

        private var app: apodgallery? = null

        fun instance(): apodgallery? {
            return app
        }
    }
}
