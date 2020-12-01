package com.machinetest.db;

import android.content.Context;
import android.os.AsyncTask;

import com.machinetest.model.GalleryImages;

import java.util.ArrayList;
import java.util.List;

public class GetImage extends AsyncTask<Void, Void, List<GalleryImages>> {

    Context mContext;
    String id;
    getImage getImage;
    String from;

    public GetImage(Context mContext, String id, getImage getImage) {
        this.mContext = mContext;
        this.id = id;
        this.getImage = getImage;
        this.from = from;
    }

    public interface getImage {
        void setImage(List<GalleryImages> myTasks);
    }


    @Override
    protected List<GalleryImages> doInBackground(Void... voids) {
        List<GalleryImages> images = new ArrayList<>();

        images = DatabaseClient
                .getInstance(mContext)
                .getAppDatabase()
                .imageDao()
                .getImage(id);
        return images;
    }


    @Override
    protected void onPostExecute(List<GalleryImages> images) {
        super.onPostExecute(images);
        getImage.setImage(images);
    }
}
