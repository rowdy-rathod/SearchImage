package com.machinetest.mvp.image_details;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.machinetest.R;
import com.machinetest.db.GetImage;
import com.machinetest.model.GalleryImages;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageDetailsActivity extends AppCompatActivity implements GetImage.getImage, ImageMVPView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.mEditTextMessageBox)
    EditText mEditTextMessageBox;
    @BindView(R.id.ButtonSubmit)
    Button ButtonSubmit;
    GalleryImages galleryImages;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    List<GalleryImages> imagesList;
    @BindView(R.id.buttonProgress)
    ProgressBar buttonProgress;
    private int isUpdate = 0;
    ImageMVPPresenterImpl imageMVPPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageMVPPresenter = new ImageMVPPresenterImpl(this, this);
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                galleryImages = (GalleryImages) getIntent().getExtras().getSerializable("Data");
                getSupportActionBar().setTitle(galleryImages.getTitle());
                Picasso.with(this)
                        .load(galleryImages.getLink())
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                imageView.setBackground(getResources().getDrawable(R.drawable.no_image));
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                imageMVPPresenter.getLocalStorageImage(galleryImages.getId());
            }
        }

    }

    @OnClick(R.id.ButtonSubmit)
    public void onViewClicked() {
        String comments = mEditTextMessageBox.getText().toString().trim();
        if (comments.equals("")) {
            Toast.makeText(this, "Enter comments..", Toast.LENGTH_SHORT).show();
        } else {
            buttonProgress.setVisibility(View.VISIBLE);
            ButtonSubmit.setVisibility(View.GONE);
            GalleryImages images = new GalleryImages();
            images.setId(galleryImages.getId());
            images.setImageDescription(comments);

            if (images != null) {
                for (int i = 0; i < imagesList.size(); i++) {
                    if (imagesList.get(i).getId().toLowerCase().contains((galleryImages.getId()).toLowerCase())) {
                        isUpdate = 1;
                    }
                }
            }
            imageMVPPresenter.saveImageLocalStorage(isUpdate,images);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
                default:
            }
        } catch (Exception e) {
            Log.e("Exception ", e.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setImage(List<GalleryImages> image) {
        if (image != null) {
            imagesList = image;
            if (imagesList != null) {
                if (!imagesList.isEmpty()) {
                    if (imagesList.get(0).getImageDescription() != null) {
                        mEditTextMessageBox.setText(image.get(0).getImageDescription());
                    }
                }
            }
        }
    }

    @Override
    public void setLocalStorageImage(List<GalleryImages> galleryImagesList) {
        this.imagesList = galleryImagesList;
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "Comments updated successfully.", Toast.LENGTH_SHORT).show();
        buttonProgress.setVisibility(View.GONE);
        ButtonSubmit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError() {
       showMessage("Comments updated successfully.");
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        buttonProgress.setVisibility(View.GONE);
        ButtonSubmit.setVisibility(View.VISIBLE);
    }
}