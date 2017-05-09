package com.team29.cse110.team29dejaphoto;

import android.app.IntentService;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by David Duplantier, Dan, and Wis on 5/9/17.
 */

public class PhotoService extends Service {

    DisplayCycle displayCycle;
    WallpaperManager background;

    private final String TAG = "PhotoService";

    @Override
    public void onCreate() {
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
}
