package com.dharanaditya.app.geopic;

import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dharanaditya.app.geopic.data.GeoPicContract;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_POSITION = "com.dharanaditya.app.geopic.EXTRA_POSITION";
    private static final int ID_PINS_LOADER = 251;
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }


    /**
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        getSupportLoaderManager().initLoader(ID_PINS_LOADER, null, this).forceLoad();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                ContentResolver contentResolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(GeoPicContract.PinsEntry.COLUMN_PIN_LATITUDE, latLng.latitude);
                contentValues.put(GeoPicContract.PinsEntry.COLUMN_PIN_LONGITUDE, latLng.longitude);

                Uri insertedUri = contentResolver.insert(GeoPicContract.PinsEntry.CONTENT_URI, contentValues);
                if (insertedUri != null) {
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                    marker.setTag(insertedUri);
                    Toast.makeText(MainActivity.this, getString(R.string.marker_added), Toast.LENGTH_SHORT).show();
                } else {
//                    Log.e(TAG, "onMapLongClick: Unable to insert pin into database.");
                }
            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        Log.d("onMarkerClick : Uri ", marker.getTag().toString());
        if (isEditMode) {
            Uri uri = Uri.parse(marker.getTag().toString());
            int deletedID = getContentResolver().delete(uri, null, null);
            if (deletedID != 0) {
                Toast.makeText(this, getString(R.string.marker_removed), Toast.LENGTH_SHORT).show();
                marker.remove();
                return true;
            }
        } else {
            Intent intent = new Intent(this, ImagesActivity.class);
            intent.putExtra(EXTRA_POSITION, marker.getPosition());
            startActivity(intent);
            return true;
        }

        return false;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {
        return new CursorLoader(this,
                GeoPicContract.PinsEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            return;
        }

        try {
            while (data.moveToNext()) {
                long pinID = data.getLong(data.getColumnIndexOrThrow(GeoPicContract.PinsEntry._ID));
                double latitude = data.getDouble(data.getColumnIndexOrThrow(GeoPicContract.PinsEntry.COLUMN_PIN_LATITUDE));
                double longitude = data.getDouble(data.getColumnIndexOrThrow(GeoPicContract.PinsEntry.COLUMN_PIN_LONGITUDE));

                LatLng latLng = new LatLng(latitude, longitude);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng));

                Uri markerUri = GeoPicContract.PinsEntry.CONTENT_URI
                        .buildUpon()
                        .appendPath(String.valueOf(pinID))
                        .build();

                marker.setTag(markerUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            data.close();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit: {
                if (isEditMode) {
                    item.setTitle(R.string.edit);
                } else {
                    item.setTitle(R.string.done);
                }
                isEditMode = !isEditMode;
                return true;
            }
            case R.id.favorites: {
                Intent intent = new Intent(this, FavoritesActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
