package com.machinetest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.machinetest.OnTab;
import com.machinetest.R;
import com.machinetest.model.GalleryImages;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<GalleryImages> galleryImages;
    public OnTab onTab;
    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;


    public GalleryImageAdapter(Context mContext, OnTab onTab) {
        this.mContext = mContext;
        this.onTab = onTab;
        galleryImages =new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.image_item_view, parent, false);
        viewHolder = new GalleryVH(v1);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return galleryImages == null ? 0 : galleryImages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == galleryImages.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }



    public void addAll(List<GalleryImages> moveResults) {
        for (GalleryImages result : moveResults) {
            add(result);
        }
    }
    public void add(GalleryImages r) {
        galleryImages.add(r);
        notifyItemInserted(galleryImages.size() - 1);
    }

    public void remove(GalleryImages r) {
        int position = galleryImages.indexOf(r);
        if (position > -1) {
            galleryImages.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new GalleryImages());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = galleryImages.size() - 1;
        GalleryImages result=null;
        if (position!=-1) {
            result = getItem(position);
        }
        if (result != null) {
            galleryImages.remove(position);
            notifyItemRemoved(position);
        }
    }
    public GalleryImages getItem(int position) {
        return galleryImages.get(position);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GalleryImages result = galleryImages.get(position); // Movie

        switch (getItemViewType(position)) {
            case ITEM:
                GalleryVH galleryVH = (GalleryVH) holder;
                galleryVH.mTextViewTitle.setText(result.getTitle());
                String imageUrl = result.getLink();
                Picasso.with(mContext)
                        .load(imageUrl)
                        .into(galleryVH.imgView, new Callback() {
                            @Override
                            public void onSuccess() {
                                galleryVH.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                galleryVH.imgView.setBackground(mContext.getResources().getDrawable(R.drawable.no_image));
                                galleryVH.progressBar.setVisibility(View.GONE);
                            }
                        });

                break;

            case LOADING:
//                Do nothing
                break;
        }
    }


    protected class GalleryVH extends RecyclerView.ViewHolder {
        @BindView(R.id.imgView)
        ImageView imgView;
        @BindView(R.id.mTextViewTitle)
        TextView mTextViewTitle;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;

        public GalleryVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onTab.onTab(galleryImages.get(getAdapterPosition()));
                }
            });
        }
    }


    protected class LoadingVH extends RecyclerView.ViewHolder {
        @BindView(R.id.progress)
        ProgressBar progress;

        public LoadingVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
