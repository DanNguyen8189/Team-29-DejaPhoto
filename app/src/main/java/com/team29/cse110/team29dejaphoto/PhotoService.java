package com.team29.cse110.team29dejaphoto;

import android.Manifest;
import android.app.IntentService;
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

    private DisplayCycle displayCycle = new DisplayCycle();
    private WallpaperManager background;
    private BroadcastReceiver receiver;

    private final String TAG = "PhotoService";
    static final float FIVE_HUNDRED_FT = 152; //number of meters in a 500 feet

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()) {
                case "NEXT_BUTTON":
                    Log.d("PhotoService", "next button intent received");
                    cycleForward();
                    break;
                case "PREV_BUTTON":
                    Log.d("PhotoService", "prev button intent received");
                    cycleBack();
                    break;
                case "KARMA":
                    Log.d("PhotoService", "karma button intent received");
                    break;
                case "RELEASE":
                    Log.d("PhotoService", "release button intent received");
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        PhotoLoader photoLoader = new DejaPhotoLoader();
        DejaPhoto[] gallery = photoLoader.getPhotosAsArray(this);
        displayCycle.fillDisplayCycle(gallery);

        IntentFilter filter = new IntentFilter();
        filter.addAction("NEXT_BUTTON");
        filter.addAction("PREV_BUTTON");
        receiver = new MyReceiver();
        registerReceiver(receiver, filter);
        super.onCreate();

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged() called.");
                Log.d(TAG, "Latitude is: " + String.valueOf(location.getLatitude()));
                Log.d(TAG, "Longitude is: " + String.valueOf(location.getLongitude()));
                displayCycle.getPriorities().setLat(location.getLatitude());
                displayCycle.getPriorities().setLong(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager =
                    (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = LocationManager.GPS_PROVIDER;
            locationManager.requestLocationUpdates(locationProvider,
                    0, FIVE_HUNDRED_FT, locationListener);
        }



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
        background = WallpaperManager.getInstance(getApplicationContext());
        DejaPhoto dejaPhoto = displayCycle.getPrevPhoto();
        Log.d(TAG, "Previous Photo was successfully retrieved");
        try {

            background.setBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), dejaPhoto.getPhotoUri()));
            Toast.makeText(this, "Displaying Photo: " + dejaPhoto.getPhotoUri(), Toast.LENGTH_SHORT).show();
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No Photo could be retrieved");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    //TODO fix toast when photo doesn't change
    public void cycleForward() {
        background = WallpaperManager.getInstance(getApplicationContext());
        DejaPhoto dejaPhoto = displayCycle.getNextPhoto();
        Log.d(TAG, "Next Photo was successfully retrieved");
        try {

            background.setBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), dejaPhoto.getPhotoUri()));
            Toast.makeText(this, "Displaying Photo: " + dejaPhoto.getPhotoUri(), Toast.LENGTH_SHORT).show();
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No Photo could be retrieved");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
