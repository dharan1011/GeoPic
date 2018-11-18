package com.dharanaditya.app.geopic.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dharanaditya.app.geopic.PreviewActivity;
import com.dharanaditya.app.geopic.R;
import com.dharanaditya.app.geopic.adapters.PhotosAdapter;
import com.dharanaditya.app.geopic.data.GeoPicContract;
import com.dharanaditya.app.geopic.interfaces.ImageItemClickListener;
import com.dharanaditya.app.geopic.model.FlickrPhoto;
import com.dharanaditya.app.geopic.network.FlickrApiTask;
import com.dharanaditya.app.geopic.utils.JsonUtils;
import com.dharanaditya.app.geopic.utils.NetworkUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ImagesFragment extends Fragment implements OnMapReadyCallback, ImageItemClickListener, FlickrApiTask.ApiTaskCallbacks {

    public static final String INTENT_MARKER_FLICKR_PHOTO = "intent_marker_flickr_photo";
    public static final String INTENT_MARKER_FLICKR_URL = "flickr_url";

    private static final String STATE_PAGE_NUMBER = "page_number";
    private static final String STATE_JSON_RESPONSE = "json_response";
    private static final String STATE_RCV = "rcv_state";

    @BindView(R.id.map_view)
    MapView mapView;
    @BindView(R.id.rcv_images)
    RecyclerView recyclerViewImages;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar loadingIndicatorProgressBar;

    private List<FlickrPhoto> photosList;
    private LatLng latLng;
    private int pageNumber;
    private PhotosAdapter photosAdapter;
    private String jsonResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);
        ButterKnife.bind(this, rootView);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        recyclerViewImages.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.span_count), StaggeredGridLayoutManager.VERTICAL);
        recyclerViewImages.setLayoutManager(staggeredGridLayoutManager);

        if (savedInstanceState == null) {
            pageNumber = 1;
            getPhotos();
        } else {
            // Restore Fragment State
            pageNumber = savedInstanceState.getInt(STATE_PAGE_NUMBER, 1);
            jsonResponse = savedInstanceState.getString(STATE_JSON_RESPONSE);
            parseAndLoadResponse();
            recyclerViewImages.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(STATE_RCV));

        }

        return rootView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                .position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    // Persisting the page and json response from api call
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        outState.putInt(STATE_PAGE_NUMBER, pageNumber);
        outState.putString(STATE_JSON_RESPONSE, jsonResponse);
        outState.putParcelable(STATE_RCV, recyclerViewImages.getLayoutManager().onSaveInstanceState());
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void refreshNewContent() {
        pageNumber = pageNumber + 1;
        getPhotos();
    }

    private void getPhotos() {
        URL photosURL = NetworkUtility.buildFlickrUrl(pageNumber, latLng);
        new FlickrApiTask(this).execute(photosURL);
    }

    private void parseAndLoadResponse() {
        try {
            photosList = JsonUtils.getFlickrPhotos(jsonResponse);
            loadPhotos();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadPhotos() {
        if (photosList == null) {
            return;
        }

        if (photosAdapter == null) {
            photosAdapter = new PhotosAdapter(getContext(), this);
            recyclerViewImages.setAdapter(photosAdapter);
        }

        photosAdapter.swapData(photosList);
    }

    @Override
    public void onPhotoClick(FlickrPhoto photo) {
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        intent.putExtra(ImagesFragment.INTENT_MARKER_FLICKR_PHOTO, photo.getUrl_m());
        intent.putExtra(INTENT_MARKER_FLICKR_URL, photo.getUrl_m());
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(FlickrPhoto flickrPhoto, String internalMediaUrl, boolean isFavorite) {
        ContentResolver contentResolver = getContext().getContentResolver();
        if (isFavorite) { // remove image
            Uri mUri = GeoPicContract.FavoritePhotosEntry.CONTENT_URI
                    .buildUpon()
                    .appendPath(flickrPhoto.getUrl_m())
                    .build();
            int deletedID = contentResolver.delete(mUri, null, null);
            if (deletedID != 0) {
                Toast.makeText(getContext(), getString(R.string.fav_remove), Toast.LENGTH_LONG).show();
            }
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL, flickrPhoto.getUrl_m());
            contentValues.put(GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL_INTERNAL, internalMediaUrl);
            contentValues.put(GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_WIDTH, flickrPhoto.getWidth_m());
            contentValues.put(GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_HEIGHT, flickrPhoto.getHeight_m());
            Uri mUri = GeoPicContract.FavoritePhotosEntry.CONTENT_URI;
            Uri insertedUri = contentResolver.insert(mUri, contentValues);
            if (insertedUri != null) {
                Toast.makeText(getContext(), getString(R.string.fav_add), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPreExecute() {
        loadingIndicatorProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostExecute(String response) {

        loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);
        if (response != null) {
            jsonResponse = response;
            parseAndLoadResponse();
        } else {
            Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }
}
