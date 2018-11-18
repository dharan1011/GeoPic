package com.dharanaditya.app.geopic.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.dharanaditya.app.geopic.R;
import com.dharanaditya.app.geopic.data.GeoPicContract;
import com.dharanaditya.app.geopic.utils.ImageUtils;

import java.io.File;

public class GeoPicGridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GeoPicGridRemoteViewsFactory(getApplicationContext());
    }
}

class GeoPicGridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private Cursor mCursor;

    public GeoPicGridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    private void getCursor() {
        Uri favoritesUri = GeoPicContract.FavoritePhotosEntry.CONTENT_URI;
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(favoritesUri, null, null, null, null);

    }

    @Override
    public void onCreate() {
        getCursor();
    }

    @Override
    public void onDataSetChanged() {
        getCursor();
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) {
            return null;
        }

        mCursor.moveToPosition(position);
        String media_url_string = mCursor.getString(mCursor.getColumnIndex(GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL));

        final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.favorite_photo_widget);

        Uri uri = Uri.parse(media_url_string);
        String imageName = uri.getLastPathSegment();
        final File internal_url = ImageUtils.getImageFileFromCache(mContext, imageName, mContext.getString(R.string.image_directory));
        Bitmap bitmap = BitmapFactory.decodeFile(internal_url.getAbsolutePath());
        remoteViews.setImageViewBitmap(R.id.widget_favorite_photo, bitmap);

        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
