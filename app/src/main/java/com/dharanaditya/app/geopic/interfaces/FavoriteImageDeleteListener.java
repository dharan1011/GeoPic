package com.dharanaditya.app.geopic.interfaces;

import java.util.List;

public interface FavoriteImageDeleteListener {

    void deletePhotos(List<String> selectedUrls);

    void onPhotoClick(String internalUrl, String flickrUrl);

}
