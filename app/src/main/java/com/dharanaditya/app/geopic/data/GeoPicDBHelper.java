package com.dharanaditya.app.geopic.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GeoPicDBHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "geopic.db";

    // TODO: 18/11/18 increment version every time you make changes
    private static final int VERSION = 1;

    public GeoPicDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_PINS = "CREATE TABLE " + GeoPicContract.PinsEntry.TABLE_NAME + " (" +
                GeoPicContract.PinsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GeoPicContract.PinsEntry.COLUMN_PIN_LATITUDE + " DOUBLE NOT NULL, " +
                GeoPicContract.PinsEntry.COLUMN_PIN_LONGITUDE + " DOUBLE NOT NULL);";

        db.execSQL(CREATE_TABLE_PINS);

        final String CREATE_TABLE_FAV_IMAGES = "CREATE TABLE " + GeoPicContract.FavoritePhotosEntry.TABLE_NAME + " (" +
                GeoPicContract.FavoritePhotosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + " TEXT NOT NULL, " +
                GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL_INTERNAL + " TEXT NOT NULL, " +
                GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_WIDTH + " INTEGER NOT NULL, " +
                GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_HEIGHT + " INTEGER NOT NULL, " +
                GeoPicContract.FavoritePhotosEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

        db.execSQL(CREATE_TABLE_FAV_IMAGES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GeoPicContract.PinsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GeoPicContract.FavoritePhotosEntry.TABLE_NAME);
        onCreate(db);
    }
}
