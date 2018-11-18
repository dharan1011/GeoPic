package com.dharanaditya.app.geopic.interfaces;

import com.dharanaditya.app.geopic.model.FlickrPhoto;

public interface ImageItemClickListener {
    void onPhotoClick(FlickrPhoto photo);

    void onFavoriteClick(FlickrPhoto flickrPhoto, String internalMediaUrl, boolean isFavorite);
}
