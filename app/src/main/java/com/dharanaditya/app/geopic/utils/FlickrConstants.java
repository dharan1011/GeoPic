package com.dharanaditya.app.geopic.utils;

import com.google.android.gms.maps.model.LatLng;

class FlickrConstants {

    // Flickr Photos Parameter Keys
    static final String METHOD = "method";
    static final String API_KEY = "api_key";
    static final String BOUNDING_BOX = "bbox";
    static final String SAFE_SEARCH = "safe_search";
    static final String EXTRAS = "extras";
    static final String KFormat = "format";
    static final String NOJSONCALLBACK = "nojsoncallback";
    static final String PER_PAGE = "per_page";
    static final String PAGE = "page";
    // Flickr Photos Parameter Values
    static final String PARAMS_SEARCH_METHOD = "flickr.photos.search";
    // TODO: 18/11/18 Replace with Flickr API Key
    static final String PARAMS_API_KEY = "FLICKR-API-KEY";
    static final String PARAMS_RESPONSE_FORMAT = "json";
    static final String PARAMS_DISABLE_JSON_CALLBACK = "1";
    static final String PARAMS_MEDIUM_URL = "url_m";
    static final String PARAMS_USE_SAFE_SEARCH = "1";
    static final String PARAMS_PER_PAGE = "20";
    // Flickr
    private static final double SEARCH_B_BOX_HALF_WIDTH = 1.0;
    private static final double SEARCH_B_BOX_HALF_HEIGHT = 1.0;
    private static final double SEARCH_LAT_RANGE_MIN = -90.0;
    private static final double SEARCH_LAT_RANGE_MAX = 90.0;
    private static final double SEARCH_LON_RANGE_MIN = -180.0;
    private static final double SEARCH_LON_RANGE_MAX = 180.0;

    static String bboxString(LatLng latLng) {
        double minimumLongitude = Math.max(latLng.longitude - SEARCH_B_BOX_HALF_WIDTH, SEARCH_LON_RANGE_MIN);
        double minimumLatitude = Math.max(latLng.latitude - SEARCH_B_BOX_HALF_HEIGHT, SEARCH_LAT_RANGE_MIN);
        double maximumLongitude = Math.max(latLng.longitude + SEARCH_B_BOX_HALF_WIDTH, SEARCH_LON_RANGE_MAX);
        double maximumLatitude = Math.max(latLng.latitude + SEARCH_B_BOX_HALF_HEIGHT, SEARCH_LAT_RANGE_MAX);

        return String.valueOf(minimumLongitude) +
                "," +
                minimumLatitude +
                "," +
                maximumLongitude +
                "," +
                maximumLatitude;
    }

}
