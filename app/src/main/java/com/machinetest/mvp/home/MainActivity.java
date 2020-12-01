package com.machinetest.mvp.home;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.machinetest.OnTab;
import com.machinetest.R;
import com.machinetest.adapter.GalleryImageAdapter;
import com.machinetest.api.Client;
import com.machinetest.api.Service;
import com.machinetest.model.GalleryImages;
import com.machinetest.mvp.image_details.ImageDetailsActivity;
import com.machinetest.receiver.NetworkChangeReceiver;
import com.machinetest.receiver.ObservableObject;
import com.machinetest.utils.PaginationScrollListener;
import com.rowdy.common_methods.methods.ConstantMethods;
import com.rowdy.common_methods.utils.Utils;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, OnTab, SearchView.OnQueryTextListener, HomeMVPView, Observer {
    Service service;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeToRefresh)
    SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress)
    MaterialProgressBar progress;
    @BindView(R.id.mTextViewHint)
    TextView mTextViewHint;
    private SearchView searchView = null;
    int spanCount = 3;
    HomeMVPPresenterImpl homeMVPPresenter;
    NetworkChangeReceiver networkChangeReceiver;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;
    private GalleryImageAdapter adapter;
    private GridLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        networkChangeReceiver = new NetworkChangeReceiver();
        service = Client.getRetrofit().create(Service.class);
        homeMVPPresenter = new HomeMVPPresenterImpl(this);
        ObservableObject.getInstance().addObserver(this);
        progress.setVisibility(View.GONE);
        swipeToRefresh.setOnRefreshListener(this);
        init();
    }


    public void init() {
        adapter = new GalleryImageAdapter(this, this);
        manager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (searchView != null) {
                            String query = searchView.getQuery().toString().trim();
                            if (!query.equals("")) {
                                homeMVPPresenter.getGalleryImageList(currentPage, query);
                            }
                        }
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unRegisterReceiver();
    }

    protected void registerReceiver() {
        registerReceiver(networkChangeReceiver, new IntentFilter(CONNECTIVITY_ACTION));
    }

    protected void unRegisterReceiver() {
        try {
            unregisterReceiver(networkChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.spanCount = 3;
            recyclerView.setLayoutManager(gridLayoutManager(3));
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.spanCount = 6;
            recyclerView.setLayoutManager(gridLayoutManager(6));
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.action_serach, menu);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if (searchItem != null) {

                searchView = (SearchView) searchItem.getActionView();
                searchView.setFitsSystemWindows(true);
                searchView.setOnQueryTextListener(this);
                searchView.setQueryHint("Search....");

                EditText txtSearch = ((EditText) searchView.findViewById(R.id.search_src_text));
                txtSearch.setHint("Search..");
                txtSearch.setHintTextColor(Color.DKGRAY);
                txtSearch.setBackgroundColor(getResources().getColor(R.color.white));
                txtSearch.setTextColor(getResources().getColor(R.color.color333333));


                View v = searchView.findViewById(R.id.search_plate);
                v.setBackgroundColor(getResources().getColor(R.color.white));

                // traverse the view to the widget containing the hint text
                LinearLayout ll = (LinearLayout) searchView.getChildAt(0);
                LinearLayout ll2 = (LinearLayout) ll.getChildAt(2);
                LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);
                SearchView.SearchAutoComplete autoComplete = (SearchView.SearchAutoComplete) ll3.getChildAt(0);

                View searchplate = (View) searchView.findViewById(R.id.search_edit_frame);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    searchplate.setBackgroundColor(getResources().getColor(R.color.white));
                }
                searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.white)));
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
                        return true;
                    }
                });
                // set the hint text color
                autoComplete.setHintTextColor(getResources().getColor(R.color.color333333));
                // set the text color
                autoComplete.setTextColor(getResources().getColor(R.color.color333333));

                ImageView searchMagIcon = (ImageView) searchView.findViewById(R.id.search_button);
                searchMagIcon.setImageResource(R.drawable.ic_search_black_24dp);

                ImageView closeIcon = searchView.findViewById(R.id.search_close_btn);
                closeIcon.setImageResource(R.drawable.ic_close_black_24dp);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onRefresh() {
        if (Utils.isNetworkConnected(this)) {
            if (searchView != null) {
                String query = searchView.getQuery().toString().trim();
                if (!query.equals("")) {
                    homeMVPPresenter.getGalleryImageList(currentPage, query);
                }
            }
            swipeToRefresh.setRefreshing(false);
        } else {
            Toast.makeText(this, "Make sure you have connected with internet.", Toast.LENGTH_SHORT).show();

        }
    }

    public GridLayoutManager gridLayoutManager(int spanCount) {
        GridLayoutManager manager = new GridLayoutManager(this, spanCount);
        return manager;
    }

    @Override
    public void onTab(Object object) {
        GalleryImages images = (GalleryImages) object;
        Intent intent = new Intent(this, ImageDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Data", images);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (Utils.isNetworkConnected(this)) {
            if (!query.equals("")) {
                homeMVPPresenter.getGalleryImageList(1, query);
            }
        } else {
            Toast.makeText(this, "Make sure you have connected with internet.", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    //    vanilla
    @Override
    public boolean onQueryTextChange(String newText) {
        if (Utils.isNetworkConnected(this)) {
            if (!newText.equals("")) {
                homeMVPPresenter.getGalleryImageList(1, newText);
            }
        } else {
            Toast.makeText(this, "Make sure you have connected with internet.", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    @Override
    public void setGalleryImageList(ArrayList<GalleryImages> galleryImageList) {
        bindList(galleryImageList);
    }

    private void bindList(ArrayList<GalleryImages> galleryImages) {
        adapter.addAll(galleryImages);

        adapter.removeLoadingFooter();
        isLoading = false;

        if (galleryImages.isEmpty()) {
            mTextViewHint.setVisibility(View.VISIBLE);
        } else {
            mTextViewHint.setVisibility(View.GONE);
        }
        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            Intent intent = (Intent) arg;
            String actionOfIntent = intent.getAction();
            boolean isConnected = Utils.isNetworkConnected(this);
            assert actionOfIntent != null;
            if (actionOfIntent.equals(CONNECTIVITY_ACTION)) {
                if (isConnected) {
                    if (searchView != null) {
                        String query = searchView.getQuery().toString().trim();
                        if (!query.equals("")) {
                            homeMVPPresenter.getGalleryImageList(1, query);
                        }
                    }
                } else {
                    ConstantMethods.showAlertDialogWithDismiss(this, "Alert!", "Make sure you have connected with internet.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}