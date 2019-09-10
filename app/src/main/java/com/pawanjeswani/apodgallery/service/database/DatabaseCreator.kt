package com.pawanjeswani.apodgallery.service.database

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import java.util.concurrent.atomic.AtomicBoolean

class DatabaseCreator {

    private val mIsDatabaseCreated = MutableLiveData<Boolean>()

    var database: AppDatabase? = null
        private set

    private val mInitializing = AtomicBoolean(true)

    /** Used to observe when the database initialization is done  */
    val isDatabaseCreated: LiveData<Boolean>
        get() = mIsDatabaseCreated

    /**
     * Creates or returns a previously-created database.
     *
     *
     * Although this uses an AsyncTask which currently uses a serial executor, it's thread-safe.
     */
    fun createDb(context: Context) {

        if (!mInitializing.compareAndSet(true, false)) {
            return  // Already initializing
        }

        mIsDatabaseCreated.value = false
        object: AsyncTask<Context, Void, Void>() {

            override fun doInBackground(vararg params: Context): Void? {
                Log.d("DatabaseCreator",
                        "Starting bg job " + Thread.currentThread().name)

                val context = params[0].applicationContext

                // Build the database!
                val db = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, AppDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()

                database = db
                return null
            }

            override fun onPostExecute(ignored: Void?) {
                // Now on the main thread, notify observers that the db is created and ready.
                mIsDatabaseCreated.value = true
            }
        }.execute(context.applicationContext)


    }

    companion object {

        private var sInstance: DatabaseCreator? = null

        // For Singleton instantiation
        private val LOCK = Any()


        @Synchronized
        fun getInstance(): DatabaseCreator?{

                if (sInstance == null) {
                    synchronized(LOCK) {
                        if (sInstance == null) {
                            sInstance = DatabaseCreator()
                        }
                    }
                }
                return sInstance
            }
    }
}
