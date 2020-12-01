package com.machinetest.mvp.image_details;

import android.content.Context;

import com.machinetest.db.GetImage;
import com.machinetest.db.SaveImage;
import com.machinetest.db.UpdateImage;
import com.machinetest.model.GalleryImages;

import java.util.List;

public class ImageMVPPresenterImpl implements ImageMVPPresenter, GetImage.getImage {
    Context mContext;
    ImageMVPView imageMVPView;

    public ImageMVPPresenterImpl(Context mContext, ImageMVPView imageMVPView) {
        this.mContext = mContext;
        this.imageMVPView = imageMVPView;
    }

    @Override
    public void getLocalStorageImage(String imageId) {
        GetImage getImage = new GetImage(mContext, imageId, this);
        getImage.execute();
    }

    @Override
    public void saveImageLocalStorage(int isUpdate, GalleryImages galleryImages) {
        if (isUpdate == 0) {
            SaveImage saveImage = new SaveImage(mContext, galleryImages);
            saveImage.execute();
        } else {
            UpdateImage updateImage = new UpdateImage(mContext, galleryImages);
            updateImage.execute();
        }
    }

    @Override
    public void setImage(List<GalleryImages> myTasks) {
        imageMVPView.setLocalStorageImage(myTasks);
    }
}
