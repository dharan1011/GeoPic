package com.dharanaditya.app.geopic.utils;

import com.dharanaditya.app.geopic.model.FlickrPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {

    // FLICKR CONSTANTS
    static final String FLICKR_RESULTS = "photos";
    static final String FLICKR_PHOTO = "photo";

    static final String FLICKR_ID = "id";
    static final String FLICKR_OWNER = "owner";
    static final String FLICKR_SECRET = "secret";
    static final String FLICKR_SERVER = "server";
    static final String FLICKR_FARM = "farm";
    static final String FLICKR_TITLE = "title";
    static final String FLICKR_IS_PUBLIC = "ispublic";
    static final String FLICKR_IS_FRIEND = "isfriend";
    static final String FLICKR_IS_FAMILY = "isfriend";
    static final String FLICKR_URL_M = "url_m";
    static final String FLICKR_HEIGHT_M = "height_m";
    static final String FLICKR_WIDTH_M = "width_m";

    public static List<FlickrPhoto> getFlickrPhotos(String flickrJSONString) throws JSONException {
        ArrayList<FlickrPhoto> flcikrReponseList = new ArrayList<>();

        JSONObject flickrJSON = new JSONObject(flickrJSONString);
        JSONObject photosJSON = flickrJSON.getJSONObject(FLICKR_RESULTS);
        JSONArray resultsArray = photosJSON.getJSONArray(FLICKR_PHOTO);

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject photoJSONObject = resultsArray.getJSONObject(i);
            String id = photoJSONObject.getString(FLICKR_ID);
            String owner = photoJSONObject.getString(FLICKR_OWNER);
            String secret = photoJSONObject.getString(FLICKR_SECRET);
            String server = photoJSONObject.getString(FLICKR_SERVER);
            int farm = photoJSONObject.getInt(FLICKR_FARM);
            String title = photoJSONObject.getString(FLICKR_TITLE);
            int isPublic = photoJSONObject.getInt(FLICKR_IS_PUBLIC);
            int isFriend = photoJSONObject.getInt(FLICKR_IS_FRIEND);
            int isFamily = photoJSONObject.getInt(FLICKR_IS_FAMILY);
            String url_m = photoJSONObject.getString(FLICKR_URL_M);
            int height_m = photoJSONObject.getInt(FLICKR_HEIGHT_M);
            int width_m = photoJSONObject.getInt(FLICKR_WIDTH_M);

            FlickrPhoto responseObject = new FlickrPhoto(id, owner, secret, server, farm, title, isPublic, isFriend, isFamily, url_m, height_m, width_m);

            flcikrReponseList.add(responseObject);
        }

        return flcikrReponseList;
    }

}
