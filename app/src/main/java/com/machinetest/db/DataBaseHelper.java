package com.machinetest.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.machinetest.model.GalleryImages;

@Database(entities = {GalleryImages.class}, version = 1)
public abstract class DataBaseHelper extends RoomDatabase {
    public abstract ImageDao imageDao();
}
