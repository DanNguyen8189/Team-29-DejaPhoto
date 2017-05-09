package com.team29.cse110.team29dejaphoto;

import android.app.IntentService;
import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by David Duplantier, Dan, and Wis on 5/9/17.
 */

public class PhotoService extends Service {

    private DisplayCycle displayCycle;
    private WallpaperManager background;
    private BroadcastReceiver receiver;

    private final String TAG = "PhotoService";

    @Override
    public void onCreate() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(intent.getAction()) {
                    case "NEXT_BUTTON":
                        Log.d("PhotoService", "next button intent received");
                        break;
                    case "PREV_BUTTON":
                        Log.d("PhotoService", "prev button intent received");
                        break;
                    case "KARMA":
                        Log.d("PhotoService", "karma button intent received");
                        break;
                    case "RELEASE":
                        Log.d("PhotoService", "release button intent received");
                        break;
                }
            }
        };
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
