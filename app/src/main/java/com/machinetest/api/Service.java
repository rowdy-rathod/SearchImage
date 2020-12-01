package com.machinetest.api;

import com.machinetest.model.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("{{page}}?")
    Call<Response> getImageGallery(
            @Query("page") int page,
            @Query("q") String query);
}
