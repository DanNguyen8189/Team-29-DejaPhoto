package com.team29.cse110.team29dejaphoto;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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


    /* Indices of columns from query (same as order defined in PROJECTIONS) */
    private final int TITLE_INDEX      = 0;
    private final int LAT_INDEX        = 1;
    private final int LONG_INDEX       = 2;
    private final int DATE_ADDED_INDEX = 3;


    private final String[] PHOTOS_PROJECTIONS = { "DATE_ADDED", "KARMA", "RELEASED" };
    private final int MILLIS_IN_SECOND = 1000;


    /* This is the Uri for the storage of all photos */
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

        SQLiteOpenHelper photoDatabaseHelper = new PhotoDatabaseHelper(context);
        SQLiteDatabase dbRead = photoDatabaseHelper.getReadableDatabase();
        SQLiteDatabase dbWrite = photoDatabaseHelper.getWritableDatabase();

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MEDIA_URI, PROJECTIONS, null, null, null);
        Cursor readCursor = dbRead.query("PHOTOS", PHOTOS_PROJECTIONS, null, null, null, null, "DATE_ADDED ASC");
        Cursor writeCursor = dbWrite.query("PHOTOS", PHOTOS_PROJECTIONS, null, null, null, null, "DATE_ADDED ASC");

        if ( cursor == null || readCursor == null || writeCursor == null ) {
            return null;
        }

        int numOfPhotos = cursor.getCount();
        int numInDB = readCursor.getCount();

        DejaPhoto[] gallery = new DejaPhoto[numOfPhotos];

        int numPhotos = 0;

        int count = 0;
        long dateAdded = 0;
        boolean skip = false;

        while ( cursor.moveToNext() ) {

            /*
            dateAdded = cursor.getLong(DATE_ADDED_INDEX);
            while (readCursor.moveToNext()) {

                if ( readCursor.getLong(0) == dateAdded ) {

                    if ( readCursor.getInt(1) == 1 ) {
                        // This photo has karma.
                        String filename = cursor.getString(TITLE_INDEX) + ".jpg";
                        String absolutePath = Environment.getExternalStorageDirectory() + "/DCIM/CAMERA/" + filename;
                        File file = new File(absolutePath);
                        Uri uri = Uri.fromFile(file);
                        Log.d(TAG, "Record found!!!!!! Photo: " + uri);

                        // TODO Check that the photo is from the camera album
                        if(file.exists()) {
                            DejaPhoto dejaPhoto = new DejaPhoto(uri,
                                    cursor.getDouble(LAT_INDEX),
                                    cursor.getDouble(LONG_INDEX),
                                    cursor.getLong(DATE_ADDED_INDEX) * MILLIS_IN_SECOND);
                            dejaPhoto.setKarma();
                            gallery[count] = dejaPhoto;
                            count++;
                            skip = true;
                        }
                    }
                    else {
                        // This photo was released
                        skip = true;
                    }
                }
                else {
                    skip = false;
                    if ( readCursor.getLong(0) > dateAdded ) {
                        break;
                    }
                }

            }

            if (skip) {
                continue;
            }

            */

            String filename = cursor.getString(TITLE_INDEX) + ".jpg";
            String absolutePath = Environment.getExternalStorageDirectory() + "/DCIM/CAMERA/" + filename;
            File file = new File(absolutePath);
            Uri uri = Uri.fromFile(file);

            //Shared Preference stores unique photoid that represents if photo was
            //released or given karma
            SharedPreferences sp = context.getSharedPreferences("Deja_Preferences", Context.MODE_PRIVATE);

            //Unique Id that would be stored if given karma
            String photoIdKarma = Long.toString(cursor.getLong(DATE_ADDED_INDEX)) + "1" + "0" + uri;

            //Unique Id that would be stored if released
            String photoIdRelease = Long.toString(cursor.getLong(DATE_ADDED_INDEX)) + "0" + "1" + uri;

            //photo is released so skip loading
            if(sp.contains(photoIdRelease))
            {
                Log.d(TAG, "WE ARE SKIPPING: " + uri + " !!!!!!!!!!!!!");
                continue;
            }

            // TODO Check that the photo is from the camera album
            if(file.exists()) {
                gallery[count] = new DejaPhoto(uri,
                        cursor.getDouble(LAT_INDEX),
                        cursor.getDouble(LONG_INDEX),
                        cursor.getLong(DATE_ADDED_INDEX) * MILLIS_IN_SECOND);

                Log.d(TAG, "Loading photo: " + uri);
                numPhotos++;
            }

            //photo has karma so give karma
            if(sp.contains(photoIdKarma)){
                gallery[count].setKarma();
            }

            count++;
        }

        cursor.close();

        Log.d(TAG, "Finished Loading, total number of photos loaded: " + numPhotos);

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
