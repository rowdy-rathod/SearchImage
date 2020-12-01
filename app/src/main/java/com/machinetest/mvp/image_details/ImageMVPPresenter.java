package com.machinetest.mvp.image_details;

import com.machinetest.model.GalleryImages;

public interface ImageMVPPresenter {
    void getLocalStorageImage(String imageId);
    void saveImageLocalStorage(int isUpdate,GalleryImages galleryImages);
}
