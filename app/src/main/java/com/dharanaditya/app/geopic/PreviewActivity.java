package com.dharanaditya.app.geopic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.dharanaditya.app.geopic.fragments.ImagesFragment;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreviewActivity extends AppCompatActivity {

    public static final String IS_INTERNAL = "isInternal";

    @BindView(R.id.imv_photo_holder)
    ImageView imageViewPhoto;
    @BindView(R.id.btn_share)
    FloatingActionButton btnShare;
    @BindView(R.id.adView)
    PublisherAdView mPublisherAdView;

    private String flickrPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);

        Intent intentCalled = getIntent();
        if (intentCalled != null) {
            String photoUrl = intentCalled.getStringExtra(ImagesFragment.INTENT_MARKER_FLICKR_PHOTO);
            flickrPhotoUrl = intentCalled.getStringExtra(ImagesFragment.INTENT_MARKER_FLICKR_URL);
            if (intentCalled.hasExtra(IS_INTERNAL)) { // load image from cache
                File filePath = new File(photoUrl);
                Picasso.get().load(filePath).into(imageViewPhoto);
            } else {
                Picasso.get().load(photoUrl).into(imageViewPhoto);
            }
        }

        mPublisherAdView.loadAd(new PublisherAdRequest.Builder().build());
    }

    @OnClick(R.id.btn_share)
    void btnShareAction(View view) {
        Intent myShareIntent;
        myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("image/*");
        myShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(flickrPhotoUrl));
        startActivity(Intent.createChooser(myShareIntent, "Share Image"));
    }

}
