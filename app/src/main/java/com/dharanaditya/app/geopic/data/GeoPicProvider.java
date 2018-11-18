package com.dharanaditya.app.geopic.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class GeoPicProvider extends ContentProvider {

    public static final int PINS = 100;
    public static final int PIN_WITH_ID = 101;
    public static final int FAV_PHOTOS = 200;
    public static final int FAV_PHOTOS_WITH_ID = 201;


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private GeoPicDBHelper mGeoPicDBHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(GeoPicContract.AUTHORITY, GeoPicContract.PATH_PINS, PINS);
        uriMatcher.addURI(GeoPicContract.AUTHORITY, GeoPicContract.PATH_PINS + "/#", PIN_WITH_ID);
        uriMatcher.addURI(GeoPicContract.AUTHORITY, GeoPicContract.PATH_FAVORITE_PHOTOS, FAV_PHOTOS);
        uriMatcher.addURI(GeoPicContract.AUTHORITY, GeoPicContract.PATH_FAVORITE_PHOTOS + "/*", FAV_PHOTOS_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mGeoPicDBHelper = new GeoPicDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case PIN_WITH_ID: {
                String pinID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{pinID};
                cursor = mGeoPicDBHelper.getReadableDatabase().query(
                        GeoPicContract.PinsEntry.TABLE_NAME,
                        projection,
                        GeoPicContract.PinsEntry._ID + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case PINS: {
                cursor = mGeoPicDBHelper.getReadableDatabase().query(
                        GeoPicContract.PinsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }
            case FAV_PHOTOS: {
                cursor = mGeoPicDBHelper.getReadableDatabase().query(
                        GeoPicContract.FavoritePhotosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }
            case FAV_PHOTOS_WITH_ID: {
                String photoID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{photoID};
                cursor = mGeoPicDBHelper.getReadableDatabase().query(
                        GeoPicContract.FavoritePhotosEntry.TABLE_NAME,
                        projection,
                        GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mGeoPicDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case PINS: {
                long id = db.insert(GeoPicContract.PinsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(GeoPicContract.PinsEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed Insert Operation [" + GeoPicContract.PinsEntry.TABLE_NAME + "] " + uri);
                }
                break;
            }
            case FAV_PHOTOS: {
                long id = db.insert(GeoPicContract.FavoritePhotosEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(GeoPicContract.FavoritePhotosEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed Insert Operation [" + GeoPicContract.FavoritePhotosEntry.TABLE_NAME + "] " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mGeoPicDBHelper.getWritableDatabase();
        int deletedRow;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PIN_WITH_ID: {
                String pinID = uri.getPathSegments().get(1);
                deletedRow = db.delete(GeoPicContract.PinsEntry.TABLE_NAME, GeoPicContract.PinsEntry._ID + "=?", new String[]{pinID});
                break;
            }
            case FAV_PHOTOS_WITH_ID: {
                String favoritePhotoID = uri.getPathSegments().get(1);
                deletedRow = db.delete(GeoPicContract.FavoritePhotosEntry.TABLE_NAME, GeoPicContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + "=?", new String[]{favoritePhotoID});
                break;
            }
            case FAV_PHOTOS: {
                deletedRow = db.delete(GeoPicContract.FavoritePhotosEntry.TABLE_NAME, whereClause, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Invalid uri: " + uri);
        }

        if (deletedRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRow;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void shutdown() {
        mGeoPicDBHelper.close();
        super.shutdown();
    }
}
