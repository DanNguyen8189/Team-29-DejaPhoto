package com.team29.cse110.team29dejaphoto.services;

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
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.team29.cse110.team29dejaphoto.utils.DejaPhotoLoader;
import com.team29.cse110.team29dejaphoto.R;
import com.team29.cse110.team29dejaphoto.utils.ReleaseSingleUser;
import com.team29.cse110.team29dejaphoto.activities.MainActivity;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;
import com.team29.cse110.team29dejaphoto.interfaces.ReleaseStrategy;
import com.team29.cse110.team29dejaphoto.models.DejaPhoto;
import com.team29.cse110.team29dejaphoto.models.DisplayCycle;
import com.team29.cse110.team29dejaphoto.models.Preferences;

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


    /* Handler to update home screen every user-customizable interval */
    private Handler handler;
    private Runnable autoUpdateTask;
    private boolean serviceRunning;

    /* Controller for release functionality */
    private ReleaseStrategy releaseController;

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

        /* Create a notification in order to start the service in the foreground so that it
         * continuously runs
         */

        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentText("effortlessly reminiscing on past times")
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification); // Start Service

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

                switch(key) {
                    case "UpdateInterval":
                        restartAutoUpdateTask();
                        Log.d(TAG, "Update interval changed");

                        return;

                    case "IsLocationOn":
                    case "IsDateOn":
                    case "IsTimeOn":
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

            }

        };
        sp.registerOnSharedPreferenceChangeListener(spListener);


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

        /* Instantiate controller for release functionality */
        releaseController = new ReleaseSingleUser(displayCycle, sp);

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
        Log.d(TAG, "Initial Update Interval: " + sp.getInt("UpdateInterval", DEFAULT_INTERVAL));

        super.onCreate();
    }

    /* Called when the service is started */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        return super.onStartCommand(intent, flags, startId);
    }

    /* Called when the service is destroyed */
    @Override
    public void onDestroy() {

        serviceRunning = false;

        if(receiver != null) unregisterReceiver(receiver);
        if(locationManager != null && locationListener != null)
            locationManager.removeUpdates(locationListener);
        if(sp != null && spListener != null)
            sp.unregisterOnSharedPreferenceChangeListener(spListener);

        Log.d(TAG, "Service stopped");

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* Method to go backwards one photo */
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

    /* Reset the user specified timer whenever they click the next or back button */
    private void restartAutoUpdateTask() {
        handler.removeCallbacks(autoUpdateTask);
        handler.postDelayed(autoUpdateTask, sp.getInt("UpdateInterval", DEFAULT_INTERVAL));
    }

    /* Method to go forwards one photo */
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

            locationTag = "No locationinfo\navailable";
        }

        // Write location info to bitmap and return
        paint.getTextBounds(locationTag, 0, locationTag.length(), rect);
        canvas.drawText(locationTag, 0, newBitmap.getHeight()-newBitmap.getHeight()/5, paint);

        Log.d(TAG, "Printed location on photo: " + locationTag);

        return newBitmap;
    }

    /*
     * This method delegates release functionality to the ReleaseSingleUser, passing along the
     * DisplayCycle and SharedPreferences.
     */
    public void releasePhoto() {

        /* If photo was successfully released, cycle forward */
        if ( releaseController.releasePhoto() == 1 ) {
            cycleForward();
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

           //Creates editor for storing unique photo ids
           SharedPreferences.Editor editor = sp.edit();
           //Unique Photo id given to a photo that has been given karma
           String photoid = Long.toString(currDisplayedPhoto.getTime().getTimeInMillis()/1000) + "1" + "0"
                   + currDisplayedPhoto.getPhotoUri();

           //stores unique photo id
           editor.putString(photoid, "Karma Photo");
           editor.apply();

           Log.d(TAG, "Photoid is: " + photoid);
       }

       else {
           Log.d(TAG, "No reference to currently displayed photo - cannot set karma, or photo already has karma");
       }

   }

}
