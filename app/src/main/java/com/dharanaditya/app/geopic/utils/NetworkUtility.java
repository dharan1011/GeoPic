package com.dharanaditya.app.geopic.utils;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtility {

    private static final String HTTPS_SCHEME = "https";
    private static final String FLICKR_API_BASE_URL = "api.flickr.com";
    private static final String FLICKR_PATH = "/services/rest";


    public static URL buildFlickrUrl(int pageNumber, LatLng latLng) {
        Uri.Builder builder = new Uri.Builder();

        Uri uri = builder.scheme(HTTPS_SCHEME)
                .authority(FLICKR_API_BASE_URL)
                .appendEncodedPath(FLICKR_PATH)
                .appendQueryParameter(FlickrConstants.METHOD, FlickrConstants.PARAMS_SEARCH_METHOD)
                .appendQueryParameter(FlickrConstants.API_KEY, FlickrConstants.PARAMS_API_KEY)
                .appendQueryParameter(FlickrConstants.SAFE_SEARCH, FlickrConstants.PARAMS_USE_SAFE_SEARCH)
                .appendQueryParameter(FlickrConstants.EXTRAS, FlickrConstants.PARAMS_MEDIUM_URL)
                .appendQueryParameter(FlickrConstants.KFormat, FlickrConstants.PARAMS_RESPONSE_FORMAT)
                .appendQueryParameter(FlickrConstants.NOJSONCALLBACK, FlickrConstants.PARAMS_DISABLE_JSON_CALLBACK)
                .appendQueryParameter(FlickrConstants.PER_PAGE, FlickrConstants.PARAMS_PER_PAGE)
                .appendQueryParameter(FlickrConstants.PAGE, String.valueOf(pageNumber))
                .appendQueryParameter(FlickrConstants.BOUNDING_BOX, FlickrConstants.bboxString(latLng))
                .build();

        URL url;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
