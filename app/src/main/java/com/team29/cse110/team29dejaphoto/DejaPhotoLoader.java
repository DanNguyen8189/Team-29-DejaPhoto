package com.team29.cse110.team29dejaphoto;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * Created by David Duplantier on 5/8/17.
 */

/*
 * This class implements the PhotoLoader interface; it provides methods to load photos from a phones
 * storage, and return them as an array of DejaPhoto objects.
 */
public class DejaPhotoLoader implements PhotoLoader {


    private final String TAG = "DejaPhotoLoader";
    /*
     * PROJECTIONS enumerates the pieces of data we want to retrieve for each photo.
     * TITLE: String for the name of a photo (e.g. "IMG_123456789").
     * LATITUDE: double value of latitude.
     * LONGITUDE: double value of the longitude.
     * DATE_ADDED: Time photo was taken, in units of milliseconds since January 1, 1970.
     */
    private final String[] PROJECTIONS = { MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.DATE_ADDED };


    /* This is the Uri to the storage of all photos */
    private final Uri MEDIA_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    /*
     * This method searches all photos retrieved from the phone's storage and returns them as an
     * array of DejaPhoto objects. This method is intended to be used only once during the app's
     * lifecycle - when the app first begins and need to load all photos into the DisplayCycle
     * object.
     */
    @Override
    public DejaPhoto[] getPhotosAsArray(Context context) {

        Log.d(TAG, "Entering getPhotosAsArray method");

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MEDIA_URI, PROJECTIONS, null, null, null);
        if ( cursor == null ) {
            return null;
        }

        int numOfPhotos = cursor.getCount();
        Log.d(TAG, "Number of photos: " + numOfPhotos);

        DejaPhoto[] gallery = new DejaPhoto[numOfPhotos];

        int titleIndex = cursor.getColumnIndex(MediaStore.Images.Media.TITLE);
        int latIndex = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
        int longIndex = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
        int timeIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);

        int count = 0;
        while ( cursor.moveToNext() ) {

            String filename = cursor.getString(titleIndex) + ".jpg";
            String absolutePath = Environment.getExternalStorageDirectory() + "/DCIM/CAMERA/" + filename;
            File file = new File(absolutePath);
            Uri uri = Uri.fromFile(file);

            gallery[count] = new DejaPhoto(uri,
                    cursor.getDouble(latIndex),
                    cursor.getDouble(longIndex),
                    cursor.getLong(timeIndex));

            count++;
        }

        cursor.close();
        return gallery;
    }

    /*
     * This method returns all photos added to the phone since the last call to getPhotosAsArray()
     * or getNewPhotosAsArray().
     */
    @Override
    public DejaPhoto[] getNewPhotosAsArray(Context context) {
        // TODO
        return new DejaPhoto[]{};
    }

}
