package com.dharanaditya.app.geopic;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.dharanaditya.app.geopic.adapters.FavoritesAdapter;
import com.dharanaditya.app.geopic.data.GeoPicContract;
import com.dharanaditya.app.geopic.fragments.ImagesFragment;
import com.dharanaditya.app.geopic.interfaces.FavoriteImageDeleteListener;
import com.dharanaditya.app.geopic.widget.GeoPicWidgetProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, FavoriteImageDeleteListener {

    public static final int INDEX_FAVORITE_URL_M = 1;
    public static final int INDEX_FAVORITE_INTERNAL_URL = 2;
    public static final int INDEX_FAVORITE_MEDIA_WIDTH = 3;
    public static final int INDEX_FAVORITE_MEDIA_HEIGHT = 4;
    public static final String[] FAVORITES_PROJECTION = {
            GeoPicContract.FavoritePhotosEntry.COLUMN_CREATED_AT,
            GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL,
            GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL_INTERNAL,
            GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_WIDTH,
            GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_HEIGHT
    };
    private static final int ID_FAVORITES_LOADER = 44;
    @BindView(R.id.rcv_fav_images)
    RecyclerView recyclerViewFavImages;
    private FavoritesAdapter mFavoritesAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);

        recyclerViewFavImages.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewFavImages.setLayoutManager(staggeredGridLayoutManager);
        mFavoritesAdapter = new FavoritesAdapter(this, this);
        recyclerViewFavImages.setAdapter(mFavoritesAdapter);

        // Start Loader
        getSupportLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this).forceLoad();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {
        switch (loaderId) {

            case ID_FAVORITES_LOADER:
                Uri favoritesUri = GeoPicContract.FavoritePhotosEntry.CONTENT_URI;
                String sortOrder = GeoPicContract.FavoritePhotosEntry.COLUMN_CREATED_AT + " DESC";

                return new CursorLoader(this,
                        favoritesUri,
                        FAVORITES_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mFavoritesAdapter.swapCursor(cursor);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        recyclerViewFavImages.smoothScrollToPosition(mPosition);
        setupAppWidget();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mFavoritesAdapter.swapCursor(null);
    }

    private void setupAppWidget() {
        GeoPicWidgetProvider.sendRefreshBroadcast(FavoritesActivity.this);
    }


    @Override
    public void deletePhotos(List<String> selectedUrls) {
        String selectedUrlsString = TextUtils.join(",", selectedUrls);
        Uri mUri = GeoPicContract.FavoritePhotosEntry.CONTENT_URI;
        int deletedID = getContentResolver().delete(mUri, GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + " IN (" + selectedUrlsString + ")", null);
        if (deletedID != 0) {
            Toast.makeText(this, getString(R.string.images_removed), Toast.LENGTH_SHORT).show();
            getSupportLoaderManager().getLoader(ID_FAVORITES_LOADER).forceLoad();
        }
    }

    @Override
    public void onPhotoClick(String internalUrl, String flickrUrl) {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(ImagesFragment.INTENT_MARKER_FLICKR_PHOTO, internalUrl);
        intent.putExtra(ImagesFragment.INTENT_MARKER_FLICKR_URL, flickrUrl);
        intent.putExtra(PreviewActivity.IS_INTERNAL, true);
        startActivity(intent);
    }
}
