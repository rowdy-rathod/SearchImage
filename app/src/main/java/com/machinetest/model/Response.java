package com.machinetest.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Response {

    @SerializedName("data")
    ArrayList<GalleryImages> galleryImages;

    public ArrayList<GalleryImages> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(ArrayList<GalleryImages> galleryImages) {
        this.galleryImages = galleryImages;
    }
}
