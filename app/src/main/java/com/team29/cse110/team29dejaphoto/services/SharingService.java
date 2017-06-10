package com.team29.cse110.team29dejaphoto.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.models.LocalPhoto;
import com.team29.cse110.team29dejaphoto.utils.DejaPhotoLoader;
import com.team29.cse110.team29dejaphoto.utils.FirebasePhotosHelper;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class SharingService extends IntentService {

    private static final String TAG = "SharingService";

    private PhotoLoader photoLoader = new DejaPhotoLoader(getSharedPreferences("Deja_Preferences", Context.MODE_PRIVATE));
    private DatabaseReference myFirebaseRef;
    //used to get the extra data added to the intent
    Bundle extras;

    FirebasePhotosHelper database
            = new FirebasePhotosHelper(getSharedPreferences("Deja_Preferences", Context.MODE_PRIVATE));

    public SharingService() {
        super("SharingServce");
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
            //onDestroy();
        }

        loadOrRemove = extras.getBoolean("loadOrRemove");
        Log.d(TAG, "Successfully passed through extra text " + loadOrRemove + " in intent");

        //if(extras.getBoolean("viewFriends"))
        //{
        //    database.downloadFriends();
        //}

        if(loadOrRemove){
            Log.d("Delete", "Uploading photos..");

            //Loads photos into an array to be uploaded
            DejaPhoto[] photos = photoLoader.getPhotosAsArray(this);

            for(int i = 0; i < photos.length; i++)
            {
                if(photos[i] == null) {
                    Log.d(TAG, "Null photo");
                    continue;
                }

                database.upload(photos[i]);
            }

        }else{
            database.deleteMyPhotos();
            Log.d("Delete", "Deleting photos from database...");
        }

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG,"Share onStartCommand.");
        if(intent.getExtras().getBoolean("sharingEnabled")) {

            database.enableSharing();
        }
        else {
            database.disableSharing();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"Share service onDestroy.");
        //database.disableSharing();
        super.onDestroy();
    }
}
