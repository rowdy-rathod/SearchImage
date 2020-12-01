package com.machinetest.mvp.home;

import com.machinetest.api.Client;
import com.machinetest.api.Service;
import com.machinetest.model.GalleryImages;
import com.machinetest.model.Response;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeMVPPresenterImpl implements HomeMVPPresenter {

    public HomeMVPView homeMVPView;
    public Service service;
    public ArrayList<GalleryImages> galleryImages;

    public HomeMVPPresenterImpl(HomeMVPView homeMVPView) {
        this.homeMVPView = homeMVPView;
        service = Client.getRetrofit().create(Service.class);
    }

    @Override
    public void getGalleryImageList(int page,String query) {
        service.getImageGallery(page,query).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                if (response.body() != null) {
                    galleryImages = response.body().getGalleryImages();
                    if (galleryImages == null) {
                        galleryImages = new ArrayList<>();
                    }
                } else {
                    galleryImages = new ArrayList<>();
                }
               homeMVPView.setGalleryImageList(galleryImages);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                galleryImages = new ArrayList<>();
                homeMVPView.setGalleryImageList(galleryImages);
            }
        });
    }
}
