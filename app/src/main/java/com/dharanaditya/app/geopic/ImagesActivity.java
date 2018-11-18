package com.dharanaditya.app.geopic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.dharanaditya.app.geopic.fragments.ImagesFragment;
import com.google.android.gms.maps.model.LatLng;

public class ImagesActivity extends AppCompatActivity {

    private LatLng latLng;
    private ImagesFragment imagesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        Intent intentResponse = getIntent();
        if (intentResponse != null) {
            latLng = intentResponse.getParcelableExtra(MainActivity.EXTRA_POSITION);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        imagesFragment = (ImagesFragment) fragmentManager.findFragmentByTag(ImagesFragment.class.getSimpleName());

        if (imagesFragment == null) {
            imagesFragment = new ImagesFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.container, imagesFragment, ImagesFragment.class.getSimpleName())
                    .commit();
        }
        imagesFragment.setLatLng(latLng);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorites: {
                Intent intent = new Intent(this, FavoritesActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.refresh: {
                if (imagesFragment != null) {
                    imagesFragment.refreshNewContent();
                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }

}
