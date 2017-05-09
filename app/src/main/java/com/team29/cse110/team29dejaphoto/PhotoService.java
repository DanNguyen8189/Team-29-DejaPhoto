package com.team29.cse110.team29dejaphoto;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by David Duplantier, Dan, and Wis on 5/9/17.
 */

public class PhotoService extends IntentService {

    DisplayCycle displayCycle;
    WallpaperManager background;

    private final String TAG = "PhotoService";

    public PhotoService() { super("PhotoService"); }

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
    protected void onHandleIntent(Intent intent) {
        Toast.makeText(this, "The Service is Running", Toast.LENGTH_SHORT).show();
    }
}
