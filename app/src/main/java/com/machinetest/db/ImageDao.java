package com.machinetest.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.machinetest.model.GalleryImages;

import java.util.List;

@Dao
public interface ImageDao {

    @Insert
    void saveImage(GalleryImages images);

    @Query("SELECT * FROM GalleryImages WHERE id IN (:id)")
    List<GalleryImages> getImage(String id);

    @Query("UPDATE GalleryImages SET image_description=(:image_description) WHERE  id IN (:id)")
    void updateImage(String id,String image_description);
}
