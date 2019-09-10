package com.pawanjeswani.apodgallery.service.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


import androidx.room.OnConflictStrategy.REPLACE
import com.pawanjeswani.apodgallery.model.dbTable.ImageData

@Dao
interface ApodDao {

    @Query("SELECT * from ImageData")
    fun getAllImages(): LiveData<List<ImageData>>

    @Insert(onConflict = REPLACE)
    fun insertImagedataToDb(imageData: ImageData)

    @Delete
    fun deleteWholeDb(imageData: ImageData)

    @Query("SELECT * from ImageData WHERE image_id= :image_id" )
    fun getImageById(image_id:String): LiveData<ImageData>


}