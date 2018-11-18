package com.dharanaditya.app.geopic.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    public static Target storePicassoImageTarget(Context context, String imageDirectory, final String imageName) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        final File directory = contextWrapper.getDir(imageDirectory, Context.MODE_PRIVATE);
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File imageFile = new File(directory, imageName);
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(imageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.i("image", "image saved to >>>" + imageFile.getAbsolutePath());
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception exception, Drawable errorDrawable) {
                if (exception != null) {
                    exception.printStackTrace();
                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

    }

    public static File getImageFileFromCache(Context context, String imageFile, String imageDirectory) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(imageDirectory, Context.MODE_PRIVATE);
        return new File(directory, imageFile);
    }

    public static void deleteFileFromCache(Context context, String imageFile, String imageDirectory) {
        ContextWrapper contextWrapper = new ContextWrapper(context);
        File directory = contextWrapper.getDir(imageDirectory, Context.MODE_PRIVATE);
        File imageFilePath = new File(directory, imageFile);
        if (imageFilePath.delete()) {
            Log.v(TAG, "image on the disk deleted successfully!");
        } else {
            Log.v(TAG, "image on the disk deleted unsuccessful");
        }
    }

}
