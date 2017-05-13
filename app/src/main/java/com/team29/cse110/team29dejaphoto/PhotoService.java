package com.team29.cse110.team29dejaphoto;

import android.Manifest;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.provider.MediaStore;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

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
        // TODO Handle no permissions and/or GPS/Network disabled
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
        // TODO More robust handling of score initialization
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

        try {
            Location location = new Location("");
            location.setLatitude(dejaPhoto.getLatitude());
            location.setLongitude(dejaPhoto.getLongitude());

            background.setBitmap(backgroundImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(),dejaPhoto.getPhotoUri()), location));
            Log.d(TAG, "Previous Photo retrieved");

            Toast.makeText(this,
                    "Displaying Photo: " + dejaPhoto.getPhotoUri(), Toast.LENGTH_SHORT).show();
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No Photo could be retrieved");
            Toast.makeText(this,
                    "End of history", Toast.LENGTH_SHORT).show();
        }

        catch (IllegalStateException e) {
            Log.d(TAG, "Uri does not exist - photo was deleted");
            cycleBack();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO fix toast when photo doesn't change
    public void cycleForward() {
        DejaPhoto dejaPhoto = displayCycle.getNextPhoto();


        try {
            Location location = new Location("");
            location.setLatitude(dejaPhoto.getLatitude());
            location.setLongitude(dejaPhoto.getLongitude());

            background.setBitmap(backgroundImage(MediaStore.Images.Media.getBitmap(this.getContentResolver(),dejaPhoto.getPhotoUri()), location));

            Toast.makeText(this, "Displaying Photo: " + dejaPhoto.getPhotoUri(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Next Photo retrieved");
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
        paint.setTextSize(10);

        // get address for location
        try {

            locationTag = list.get(0).getAddressLine(0);
        }// if no valid location
        catch(Exception e) {

            locationTag = "No location info\navailable";
        }

        // Write location info to bitmap and return
        paint.getTextBounds(locationTag, 0, locationTag.length(), rect);
        canvas.drawText(locationTag, 0, newBitmap.getHeight() - 30, paint);
        return newBitmap;
    }

}
