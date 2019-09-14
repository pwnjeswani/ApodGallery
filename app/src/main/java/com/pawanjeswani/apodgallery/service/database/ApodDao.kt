package com.pawanjeswani.apodgallery.service.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.pawanjeswani.apodgallery.model.dbTable.ImageData

@Dao
interface ApodDao {

    @Query("SELECT * from ImageData ORDER BY image_id DESC")
    fun getAllImages(): LiveData<List<ImageData>>

    @Insert(onConflict = REPLACE)
    fun insertImagedataToDb(imageData: ImageData)

    @Insert(onConflict = REPLACE)
    fun insetrmultiImges(list:List<ImageData>)

    @Delete
    fun deleteWholeDb(imageData: ImageData)

    @Query("SELECT * from ImageData WHERE image_id= :image_id" )
    fun getImageById(image_id:String): LiveData<ImageData>

    @Query("SELECT * from ImageData WHERE image_id > :image_id  ORDER BY image_id DESC")
    fun getNextImages(image_id: String) : LiveData<List<ImageData>>

    @Query("SELECT * from ImageData WHERE image_id < :image_id ORDER BY image_id DESC")
    fun getPrevImages(image_id: String) : LiveData<List<ImageData>>


    @Query("SELECT * from ImageData WHERE image_id>:image_id  ORDER BY  image_id ASC LIMIT 10")
    fun getPreviousTenImages(image_id: String) : LiveData<List<ImageData>>

    @Query("SELECT COUNT(image_id) from ImageData WHERE image_id =:image_id")
    fun isApodStored(image_id: String): Int

    @Query("SELECT COUNT(*) FROM imagedata")
    fun getSizeOfDb():Int

}