package com.machinetest.db;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.machinetest.model.GalleryImages;


public class UpdateImage extends AsyncTask<Void, Void, Void> {
    Context mContext;
    GalleryImages galleryImages;

    public UpdateImage(Context mContext, GalleryImages galleryImages) {
        this.mContext = mContext;
        this.galleryImages = galleryImages;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        //adding to database
        DatabaseClient.getInstance(mContext).getAppDatabase()
                .imageDao()
                .updateImage(galleryImages.getId(),galleryImages.getImageDescription());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Log.e("Success=>", "Successfully uploaded");

    }
}
