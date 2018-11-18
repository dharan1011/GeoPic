package com.dharanaditya.app.geopic.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dharanaditya.app.geopic.data.GeoPicContract;
import com.dharanaditya.app.geopic.data.GeoPicDBHelper;

public class PhotoAdapterHelper {
    private SQLiteDatabase db;
    private GeoPicDBHelper dbHelper;

    public PhotoAdapterHelper(Context context) {
        dbHelper = new GeoPicDBHelper(context);
        openReadableDB();
    }

    //Open DB Readable
    private void openReadableDB() {
        try {
            db = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        try {
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFavorite(String photoURL) {
        String[] selectionArguments = new String[]{photoURL};
        Cursor cursor = db.query(GeoPicContract.FavoritePhotosEntry.TABLE_NAME, null, GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + " = ?", selectionArguments, null, null, null);
        return (cursor != null && cursor.moveToFirst());
    }

}
