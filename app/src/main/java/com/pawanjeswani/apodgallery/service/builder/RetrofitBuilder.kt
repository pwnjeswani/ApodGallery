package com.pawanjeswani.apodgallery.service.builder

import com.pawanjeswani.apodgallery.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitBuilder {


    //private static Retrofit customBaseUrlRetrofit = null;

    private var retrofit: Retrofit? = null
    val instance: Retrofit?
        get() {
            if (retrofit == null) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.BODY
                val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

                val builder = Retrofit.Builder()
                        .baseUrl( BuildConfig.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                if (BuildConfig.DEBUG) {
                    builder.client(client)
                }
                retrofit = builder.build()
            }
            return retrofit
        }
}
