package com.team29.cse110.team29dejaphoto;

import android.app.IntentService;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by David Duplantier, Dan, and Wis on 5/9/17.
 */

public class PhotoService extends Service {

    private DisplayCycle displayCycle = new DisplayCycle();
    private WallpaperManager background;
    private BroadcastReceiver receiver;

    private final String TAG = "PhotoService";

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service stopped");
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
