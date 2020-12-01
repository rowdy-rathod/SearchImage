package com.machinetest.mvp.image_details;

import com.machinetest.model.GalleryImages;

import java.util.List;

public interface ImageMVPView {

    void setLocalStorageImage(List<GalleryImages> galleryImagesList);

    void onSuccess();
    void onError();
}
