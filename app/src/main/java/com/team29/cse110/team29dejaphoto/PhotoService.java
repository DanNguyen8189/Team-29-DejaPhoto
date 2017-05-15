package com.team29.cse110.team29dejaphoto;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import java.util.List;
import java.util.Locale;

/**
 * Created by David Duplantier, Dan, Wis and Brian on 5/9/17.
 */

public class PhotoService extends Service {

    /* Context */
    Context context = this;

    /* Underlying Object that handles image cycling */
    private DisplayCycle displayCycle;

    /* Home screen background setter */
    private WallpaperManager background;
    private DejaPhoto currDisplayedPhoto;

    Location myLocation;

    /* Observers */
    private BroadcastReceiver receiver;
    private LocationListener locationListener;
    private LocationManager locationManager;

    /* SharedPreferences */
    private SharedPreferences sp;
    private SharedPreferences.OnSharedPreferenceChangeListener spListener;

    /* Constants */
    private static final String TAG = "PhotoService";
    private static final float FIVE_HUNDRED_FT = 152;   // Number of meters in a 500 feet
    private static final long TWO_HOURS = 7200000;      // Two hours in milliseconds
    private static final int PAINT_SIZE_CONSTANT = 50;  // Constant to derive brush size
    private static final int DEFAULT_INTERVAL = 300000;

    /* Database for storing released and karma information */
    private PhotoDatabaseHelper DbHelper;
    private SQLiteDatabase db;

    /* Handler to update home screen every user-customizable interval */
    private Handler handler;
    private Runnable autoUpdateTask;
    private boolean serviceRunning;


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

                    givePhotoKarma();
                    break;

                case "RELEASE_BUTTON":
                    Log.d(TAG, "Release button intent received");

                    releasePhoto();
                    break;
            }
        }
    }

    @Override
    public void onCreate() {

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);

        /* Forward Permissions Check */
        // TODO Handle no permissions and/or GPS/Network disabled
        if(!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)) return;

        /* Initialize WallpaperManager object */
        background = WallpaperManager.getInstance(getApplicationContext());

        /* Location Observer */
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged() called.");
                Log.d(TAG, "Latitude is: " + String.valueOf(location.getLatitude()));
                Log.d(TAG, "Longitude is: " + String.valueOf(location.getLongitude()));

                displayCycle.updatePriorities(location,
                        new Preferences(
                                sp.getBoolean("IsLocationOn", true),
                                sp.getBoolean("IsDateOn", true),
                                sp.getBoolean("IsTimeOn", true)
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

        /* Initialize SharedPreferences object */
        sp = getSharedPreferences("Deja_Preferences", Context.MODE_PRIVATE);

        /* Listen to changes to SharedPreferences */
        spListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                if(key == "UpdateInterval") {
                    restartAutoUpdateTask();
                    return;
                }

                if(!(ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(
                                context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED)) return;

                displayCycle.updatePriorities(
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER),
                        new Preferences(
                                sp.getBoolean("IsLocationOn", true),
                                sp.getBoolean("IsDateOn", true),
                                sp.getBoolean("IsTimeOn", true)
                        )
                );
                Log.d(TAG, "Preferences changed, display cycle updated accordingly");
            }

        };
        sp.registerOnSharedPreferenceChangeListener(spListener);

        /* Database for karma/release */
        DbHelper = new PhotoDatabaseHelper(this);
        db = DbHelper.getWritableDatabase();

        /* Handles initializing receiver and binding filter to only receive widget intents */

        IntentFilter filter = new IntentFilter();
        filter.addAction("NEXT_BUTTON");
        filter.addAction("PREV_BUTTON");
        filter.addAction("KARMA_BUTTON");
        filter.addAction("RELEASE_BUTTON");

        receiver = new MyReceiver();
        registerReceiver(receiver, filter);

        /* Initializes DisplayCycle with photos from the system */

        PhotoLoader photoLoader = new DejaPhotoLoader();

        displayCycle = new DisplayCycle(photoLoader.getPhotosAsArray(this));
        // TODO More robust handling of score initialization
        displayCycle.updatePriorities(
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER),
                new Preferences(
                        sp.getBoolean("IsLocationOn", true),
                        sp.getBoolean("IsDateOn", true),
                        sp.getBoolean("IsTimeOn", true)
                )
        );

        /* Indicate to the user that photos have been loaded */
        Toast.makeText(this, "Done Loading Photos", Toast.LENGTH_SHORT).show();

        /* Handles updating the home screen every configurable interval */

        serviceRunning = true;

        handler = new Handler();
        autoUpdateTask = new Runnable() {
            @Override
            public void run() {
                cycleForward();

                if(serviceRunning)
                    handler.postDelayed(autoUpdateTask, sp.getInt("UpdateInterval", DEFAULT_INTERVAL));
            }
        };
        handler.postDelayed(autoUpdateTask, sp.getInt("UpdateInterval", DEFAULT_INTERVAL));
        Log.d(TAG, "INTERVAL IN MILLIS: " + sp.getInt("UpdateInterval", DEFAULT_INTERVAL));

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

        serviceRunning = false;
        if(receiver != null) unregisterReceiver(receiver);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void cycleBack() {

        /* Restart handler's autoUpdate task */

        restartAutoUpdateTask();

        /* Set previous photo */

        DejaPhoto dejaPhoto = displayCycle.getPrevPhoto();
        if ( dejaPhoto != null ) {
            currDisplayedPhoto = dejaPhoto;
        }

        try {
            background.setBitmap(backgroundImage(
                    MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            dejaPhoto.getPhotoUri()),
                    dejaPhoto.getLocation())
            );

            Log.d(TAG, "Displaying Previous Photo: " + dejaPhoto.getPhotoUri()
                    + " (" + dejaPhoto.getScore() + ")");
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No Photo could be retrieved");
            Toast.makeText(this, "End of history", Toast.LENGTH_SHORT).show();
        }

        catch (IllegalStateException e) {
            Log.d(TAG, "Uri does not exist - photo was deleted");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restartAutoUpdateTask() {
        handler.removeCallbacks(autoUpdateTask);
        handler.postDelayed(autoUpdateTask, sp.getInt("UpdateInterval", DEFAULT_INTERVAL));
    }

    public void cycleForward() {

        /* Restart handler's autoUpdate task */

        restartAutoUpdateTask();

        /* Set next photo */

        DejaPhoto dejaPhoto = displayCycle.getNextPhoto();
        if ( dejaPhoto != null ) {
            currDisplayedPhoto = dejaPhoto;
        }

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
        Geocoder geocoder;
        List<Address> list;

        // Generate new bitmap and paint objects for modification
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setColor(Color.RED);
        paint.setTextSize(bitmap.getHeight() / PAINT_SIZE_CONSTANT);

        // get address for location
        try {

            // Geocoder to get address from remote server
            geocoder = new Geocoder(this, Locale.getDefault());
            list = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(),1);

            locationTag = list.get(0).getAddressLine(0);

        }

        // if no valid location
        catch(Exception e) {

            locationTag = "No location info\navailable";
        }

        // Write location info to bitmap and return
        paint.getTextBounds(locationTag, 0, locationTag.length(), rect);
        canvas.drawText(locationTag, 0, newBitmap.getHeight()-newBitmap.getHeight()/5, paint);

        Log.d(TAG, "Printed location on photo: " + locationTag);

        return newBitmap;
    }

    /*
     * This method delegates the DisplayCycle to find the currently displayed photo, release it
     * from displayCycle, and enter new record in the database. Afterwards, the method cycles to
     * the next photo.
     */
    public void releasePhoto() {

        if ( currDisplayedPhoto != null ) {
            Log.d(TAG, "Releasing currently displayed photo");
            currDisplayedPhoto.setReleased();
            PhotoDatabaseHelper.insertPhoto(db, currDisplayedPhoto.getTime().getTimeInMillis(), 0, 1);

            SharedPreferences.Editor editor = sp.edit();
            String photoid = Long.toString(currDisplayedPhoto.getTime().getTimeInMillis()/1000) + "0" + "1";
            editor.putString(photoid, "Release Photo");
            editor.apply();
            Log.d(TAG, "Photoid is: " + photoid);

            displayCycle.removeCurrPhotoFromHistory();
            cycleForward();


        }
        else {
            Log.d(TAG, "No reference to currently displayed photo - cannot release");
        }

   }

   /*
    * This method gives karma to the currently displayed photo, then enters a new record in the
    * database.
    */
   public void givePhotoKarma() {

       if ( currDisplayedPhoto != null && !currDisplayedPhoto.getKarma() ) {
           Log.d(TAG, "Setting karma on currently displayed photo");
           currDisplayedPhoto.setKarma();
           if(!(ActivityCompat.checkSelfPermission(
                   context, Manifest.permission.ACCESS_FINE_LOCATION)
                   == PackageManager.PERMISSION_GRANTED &&
                   ActivityCompat.checkSelfPermission(
                           context, Manifest.permission.ACCESS_COARSE_LOCATION)
                           == PackageManager.PERMISSION_GRANTED)) return;
           currDisplayedPhoto.updateScore(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER),
                   new Preferences(
                           sp.getBoolean("IsLocationOn", true),
                           sp.getBoolean("IsDateOn", true),
                           sp.getBoolean("IsTimeOn", true)));
           PhotoDatabaseHelper.insertPhoto(db, currDisplayedPhoto.getTime().getTimeInMillis(), 1, 0);

           SharedPreferences.Editor editor = sp.edit();
           String photoid = Long.toString(currDisplayedPhoto.getTime().getTimeInMillis()/1000) + "1" + "0";
           editor.putString(photoid, "Karma Photo");
           editor.apply();
           Log.d(TAG, "Photoid is: " + photoid);
       }
       else {
           Log.d(TAG, "No reference to currently displayed photo - cannot set karma, or photo already has karma");
       }

   }

}
