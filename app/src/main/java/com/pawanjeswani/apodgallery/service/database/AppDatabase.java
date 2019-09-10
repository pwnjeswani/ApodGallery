package com.pawanjeswani.apodgallery.service.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.pawanjeswani.apodgallery.model.dbTable.ImageData;
import com.pawanjeswani.apodgallery.util.IntArrayConverter;
import com.pawanjeswani.apodgallery.util.StringArrayConverter;

@Database(entities = {ImageData.class} ,version = AppDatabase.DATABASE_VERSION,
        exportSchema = false)
@TypeConverters({IntArrayConverter.class, StringArrayConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "ApodImages.db";

    public abstract ApodDao getApodDao();

}