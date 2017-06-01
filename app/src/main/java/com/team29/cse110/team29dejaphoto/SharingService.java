package com.team29.cse110.team29dejaphoto;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class SharingService extends IntentService {

    private static final String TAG = "SharingService";


    //used to get the extra data added to the intent
    Bundle extras;

    FirebasePhotosHelper database = new FirebasePhotosHelper();

    public SharingService() {
        super("SharingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Boolean loadOrRemove; // used to know if we need to load to or remove from database
        // get the extra data added to intent
        extras = intent.getExtras();

        // stop the service if we did not correctly pass in extra data in intent
        if (extras == null){
            Log.d(TAG, "Error: did not correctly modify intent to let us know whether to add or" +
                    " remove your photos from database");
            onDestroy();
        }

        loadOrRemove = extras.getBoolean("loadOrRemove");
        Log.d(TAG, "Successfully passed through extra text " + loadOrRemove + " in intent");

        if(loadOrRemove){
            //TODO load photos onto database
            database.upload(this);
        }else{
            //TODO remove photos from database
        }

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG,"Share onStartCommand.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"Share service onDestroy.");
        super.onDestroy();
    }
}
