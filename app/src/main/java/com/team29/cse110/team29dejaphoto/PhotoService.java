package com.team29.cse110.team29dejaphoto;

import android.Manifest;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by David Duplantier, Dan, Wis and Brian on 5/9/17.
 */

public class PhotoService extends Service {

    /* Underlying Object that handles image cycling */
    private DisplayCycle displayCycle;

    /* Homescreen background setter */
    private WallpaperManager background;

    /* Observers */
    private BroadcastReceiver receiver;
    private LocationListener locationListener;
    private LocationManager locationManager;

    /* CONSTANTS */
    private static final String TAG = "PhotoService";
    private static final float FIVE_HUNDRED_FT = 152; //number of meters in a 500 feet


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

                    break;
            }
        }
    }

    @Override
    public void onCreate() {

        /* Forward Permissions Check */
        if(!(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
           ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) stopSelf();

        /* Initialize WallpaperManager object */
        background = WallpaperManager.getInstance(getApplicationContext());

        /* Handles initializing receiver and binding filter to only receive widget intents */

        IntentFilter filter = new IntentFilter();
        filter.addAction("NEXT_BUTTON");
        filter.addAction("PREV_BUTTON");

        receiver = new MyReceiver();
        registerReceiver(receiver, filter);

        /* Location Observer */
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged() called.");
                Log.d(TAG, "Latitude is: " + String.valueOf(location.getLatitude()));
                Log.d(TAG, "Longitude is: " + String.valueOf(location.getLongitude()));

                displayCycle.updatePriorities(location);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, FIVE_HUNDRED_FT, locationListener);

        /* Initializes DisplayCycle with photos from the system */

        PhotoLoader photoLoader = new DejaPhotoLoader();

        displayCycle = new DisplayCycle(photoLoader.getPhotosAsArray(this));
        displayCycle.updatePriorities(
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        );

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

        unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // TODO fix toast when photo doesn't change
    public void cycleBack() {
        DejaPhoto dejaPhoto = displayCycle.getPrevPhoto();
        Log.d(TAG, "Previous Photo retrieved");

        try {
            background.setBitmap(
                    MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                                      dejaPhoto.getPhotoUri())
            );
            Toast.makeText(this,
                    "Displaying Photo: " + dejaPhoto.getPhotoUri(), Toast.LENGTH_SHORT).show();
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No Photo could be retrieved");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO fix toast when photo doesn't change
    public void cycleForward() {
        DejaPhoto dejaPhoto = displayCycle.getNextPhoto();
        Log.d(TAG, "Next Photo retrieved");

        try {
            background.setBitmap(
                    MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                                      dejaPhoto.getPhotoUri())
            );
            Toast.makeText(this,
                    "Displaying Photo: " + dejaPhoto.getPhotoUri(), Toast.LENGTH_SHORT).show();
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No Photo could be retrieved");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
