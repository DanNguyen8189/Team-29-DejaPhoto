package com.team29.cse110.team29dejaphoto;

import android.Manifest;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by David Duplantier, Dan, Wis and Brian on 5/9/17.
 */

public class PhotoService extends Service {

    /* Underlying Object that handles image cycling */
    private DisplayCycle displayCycle;

    /* Home screen background setter */
    private WallpaperManager background;

    /* Observers */
    private BroadcastReceiver receiver;
    private LocationListener locationListener;
    private LocationManager locationManager;

    /* SharedPreferences */
    private SharedPreferences sp;

    /* Constants */
    private static final String TAG = "PhotoService";
    private static final float FIVE_HUNDRED_FT = 152; //number of meters in a 500 feet
    private static final long TWO_HOURS = 7200000; // Two hours in milliseconds
    private static final int PAINT_SIZE_CONSTANT = 50;


    /* Database for storing released and karma information */
    //private PhotoDatabaseHelper DbHelper = new PhotoDatabaseHelper(this);
    //private SQLiteDatabase db = DbHelper.getWritableDatabase();




    /**
     * Custom Widget Action Receiver inner class
     */
    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case "NEXT_BUTTON":
                    Log.d(TAG, "Next button intent received");

                    cycleForward();
                    break;

                case "PREV_BUTTON":
                    Log.d(TAG, "Prev button intent received");

                    cycleBack();
                    break;

                case "KARMA_BUTTON":
                    Log.d(TAG, "Karma button intent received");

                    break;

                case "RELEASE_BUTTON":
                    Log.d(TAG, "Release button intent received");

                    //releasePhoto();
                    break;
            }
        }
    }

    @Override
    public void onCreate() {

        /* Forward Permissions Check */
        // TODO Handle no permissions and/or GPS/Network disabled
        if(!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)) return;

        /* Initialize WallpaperManager object */
        background = WallpaperManager.getInstance(getApplicationContext());

        /* Initialize SharedPreferences object */
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        /* Handles initializing receiver and binding filter to only receive widget intents */

        IntentFilter filter = new IntentFilter();
        filter.addAction("NEXT_BUTTON");
        filter.addAction("PREV_BUTTON");
        filter.addAction("KARMA_BUTTON");
        filter.addAction("RELEASE_BUTTON");

        receiver = new MyReceiver();
        registerReceiver(receiver, filter);

        /* Location Observer */
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged() called.");
                Log.d(TAG, "Latitude is: " + String.valueOf(location.getLatitude()));
                Log.d(TAG, "Longitude is: " + String.valueOf(location.getLongitude()));

                displayCycle.updatePriorities(location,
                        new Preferences(
                            sp.getBoolean("isLocationOn", true),
                            sp.getBoolean("isDateOn", true),
                            sp.getBoolean("isTimeOn", true)
                        )
                );
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        /* Initializes and configures the LocationListener */
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // TODO Add time
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, FIVE_HUNDRED_FT, locationListener);

        /* Initializes DisplayCycle with photos from the system */

        PhotoLoader photoLoader = new DejaPhotoLoader();

        displayCycle = new DisplayCycle(photoLoader.getPhotosAsArray(this));
        // TODO More robust handling of score initialization
        displayCycle.updatePriorities(
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER),
                new Preferences(
                        sp.getBoolean("isLocationOn", true),
                        sp.getBoolean("isDateOn", true),
                        sp.getBoolean("isTimeOn", true)
                )
        );

        Toast.makeText(this, "Done Loading Photos", Toast.LENGTH_SHORT).show();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service stopped");

        if(receiver != null) unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void cycleBack() {
        DejaPhoto dejaPhoto = displayCycle.getPrevPhoto();

        try {
            background.setBitmap(backgroundImage(
                    MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            dejaPhoto.getPhotoUri()),
                    dejaPhoto.getLocation())
            );

            Log.d(TAG, "Displaying Previous Photo: " + dejaPhoto.getPhotoUri());
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No Photo could be retrieved");
            Toast.makeText(this, "End of history", Toast.LENGTH_SHORT).show();
        }

        catch (IllegalStateException e) {
            Log.d(TAG, "Uri does not exist - photo was deleted");
            cycleBack();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cycleForward() {
        DejaPhoto dejaPhoto = displayCycle.getNextPhoto();

        try {
            background.setBitmap(backgroundImage(
                    MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            dejaPhoto.getPhotoUri()),
                    dejaPhoto.getLocation())
            );

            Log.d(TAG, "Displaying Next Photo: "
                    + dejaPhoto.getPhotoUri() + " (" + dejaPhoto.getScore() + ")");
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No Photo could be retrieved");
        }

        catch (IllegalStateException e) {
            Log.d(TAG, "Uri not longer exists - this photo was deleted.");
            cycleForward();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method takes a bitmap image and location information, and returns a modified bitmap
     * with location info in the bottom left corner. If no information is available (i.e. location
     * is empty), appropriate text is printed.
     *
     * @param bitmap the background image to be modified
     * @param location location info of the image
     * @return returns image with location info as bitmap
     * @throws Exception ArrayIndexOutOfBounds when no location info
     */
    public Bitmap backgroundImage(Bitmap bitmap, Location location) throws Exception {

        Log.d(TAG, "Writing address to bitmap");

        String locationTag;

        // Geocoder to get address from remote server
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> list = geocoder.getFromLocation(location.getLatitude(),
                                                      location.getLongitude(),1);

        // Generate new bitmap and paint objects for modification
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setColor(Color.RED);
        paint.setTextSize(bitmap.getHeight() / PAINT_SIZE_CONSTANT);

        // get address for location
        try {

            locationTag = list.get(0).getAddressLine(0);
        }

        // if no valid location
        catch(Exception e) {

            locationTag = "No location info\navailable";
        }

        // Write location info to bitmap and return
        paint.getTextBounds(locationTag, 0, locationTag.length(), rect);
        canvas.drawText(locationTag, 0, newBitmap.getHeight() - 30, paint);

        Log.d(TAG, "Printed location on photo: " + locationTag);

        return newBitmap;
    }



    //public void releasePhoto()
   //{
   //     displayCycle.release(db);
    //    cycleForward();
   // }

}
