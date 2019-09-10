package com.pawanjeswani.apodgallery

import android.app.Application

class Apodgallery : Application() {


    override fun onCreate() {

        super.onCreate()
        app = this
//        SharedPrefUtil.set(this)
//        DataRepository.instance()
//        DatabaseCreator.getInstance()!!.createDb(this)
    }

    companion object {

        private var app: Apodgallery? = null

        fun instance(): Apodgallery? {
            return app
        }
    }
}
