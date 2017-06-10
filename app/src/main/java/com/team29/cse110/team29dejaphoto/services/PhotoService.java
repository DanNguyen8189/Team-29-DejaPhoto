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
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team29.cse110.team29dejaphoto.R;
import com.team29.cse110.team29dejaphoto.activities.MainActivity;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;
import com.team29.cse110.team29dejaphoto.interfaces.ReleaseStrategy;
import com.team29.cse110.team29dejaphoto.models.DisplayCycleMediator;
import com.team29.cse110.team29dejaphoto.models.LocalPhoto;
import com.team29.cse110.team29dejaphoto.models.Preferences;
import com.team29.cse110.team29dejaphoto.models.RemotePhoto;
import com.team29.cse110.team29dejaphoto.utils.BitmapUtil;
import com.team29.cse110.team29dejaphoto.utils.DejaPhotoLoader;
import com.team29.cse110.team29dejaphoto.utils.ReleaseSingleUser;

import org.apache.commons.io.FileUtils;

import java.io.File;


/**
 * Created by David Duplantier, Dan, Wis and Brian on 5/9/17.
 */

public class PhotoService extends Service {

    /* Context */
    Context context = this;

    /* Underlying Object that handles image cycling */
    private DisplayCycleMediator displayCycle;

    /* Home screen background setter */
    private WallpaperManager background;
    private DejaPhoto currDisplayedPhoto;

    /* Observers */
    private BroadcastReceiver receiver;
    private LocationListener locationListener;
    private LocationManager locationManager;

    /* SharedPreferences */
    private SharedPreferences dejaPreferences;
    private SharedPreferences sp;
    private SharedPreferences.OnSharedPreferenceChangeListener spListener;

    /* Constants */
    public static final String DEJA_PREFS = "Deja_Preferences"; // SharedPreference file key
    private static final String TAG = "PhotoService";
    private static final float FIVE_HUNDRED_FT = 152;   // Number of meters in a 500 feet
    private static final int DEFAULT_INTERVAL = 300000;
    final long FIVE_MEGABYTES = 5*1024 * 1024;
    static boolean sharingSetting;



    BitmapUtil bitmapUtil = new BitmapUtil(this);


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

                case "EDIT_LOCATION":
                    Log.d(TAG, "Edit location intent received");

                    if (currDisplayedPhoto == null) {
                        Toast.makeText(context, "No Photo Available", Toast.LENGTH_SHORT).show();
                    }

                    else if (currDisplayedPhoto instanceof RemotePhoto) {
                       Toast.makeText(context, "Cannot edit location of friend's photo",
                               Toast.LENGTH_SHORT).show();
                    }
                    else {
                        currDisplayedPhoto.setCustomLocation(intent.getStringExtra("customLocation"));

                        //record change in sharedPreferences
                        dejaPreferences = getSharedPreferences(DEJA_PREFS, 0);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(currDisplayedPhoto.getPhotoUri().toString(), intent.getStringExtra("customLocation"));
                        editor.commit();

                        Toast.makeText(context, "Your location was successfully set!",
                                Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "location was set for photo with this URI: " + currDisplayedPhoto.getPhotoUri().toString());
                    }
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


                    case "IsViewingFriends":
                        Log.d(TAG, "IsViewingFriends changed to " + sp.getBoolean("IsViewingFriends",true));
                        //Firebase reference for accessing stored media
                        final FirebaseStorage storage = FirebaseStorage.getInstance();
                        final StorageReference storageRef = storage.getReference();

                        if(sp.getBoolean("IsViewingFriends",true)) {

                            String path2 = Environment.getExternalStorageDirectory() + "/DejaPhotoFriends";
                            File DejaPhotoFriends = new File(path2);
                            // Create the DejaPhotoFriends album if it doesn't already exist
                            if (!DejaPhotoFriends.exists()){
                                DejaPhotoFriends.mkdirs();
                                Log.d(TAG, "DejaPhotoFriends directory created");
                            }

                            //Firebase reference for getting user information
                            FirebaseDatabase database;
                            final DatabaseReference myFirebaseRef;
                            FirebaseUser user;


                            //Gets current User
                            database = FirebaseDatabase.getInstance();
                            myFirebaseRef = database.getReference();
                            user = FirebaseAuth.getInstance().getCurrentUser();

                            if(user != null) {
                                String userName = user.getEmail().substring(0, user.getEmail().indexOf('@'));

                                //For storing photos in album
                                final String path = Environment.getExternalStorageDirectory() + "/DejaPhotoFriends";

                                //Sets reference to current user in realtime database
                                DatabaseReference dataFriendsRef = myFirebaseRef.child(userName).child("friends");
                                Query friendsQuery = dataFriendsRef;

                                friendsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (final DataSnapshot friend : dataSnapshot.getChildren()) {
                                            Log.d(TAG, "Friend is: " + friend.getKey());
                                            final StorageReference storageUserRef = storageRef.child(friend.getKey());
                                            final Query friendsPhotos = myFirebaseRef.child(friend.getKey()).child("Photos");

                                            //Checks friend's sharing setting before downloading photos
                                            Query friendsSharing = myFirebaseRef.child(friend.getKey()).child("SharingEnabled");
                                            friendsSharing.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Log.d(TAG, "Setting is " + dataSnapshot.getValue());
                                                    sharingSetting = ((boolean) dataSnapshot.getValue());

                                                    //Will download photos if friend has setting enabled
                                                    Log.d(TAG, "Sharing is " + sharingSetting);
                                                    if (sharingSetting) {

                                                        Log.d(TAG, "Entering friends photos ");
                                                        friendsPhotos.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                                for (final DataSnapshot friendPhotoRef : dataSnapshot.getChildren()) {
                                                                    //Gets reference to friend's photos and downloads them to
                                                                    Log.d(TAG, "Downloading " + friend.getKey() + "'s " +
                                                                            friendPhotoRef.getKey() + ".jpg");
                                                                    StorageReference photoref = storageUserRef.child(friendPhotoRef.getKey() + ".jpg");
                                                                    //creates new file for photo in album
                                                                    File friendPhoto = new File(path + "/" + friendPhotoRef.getKey() + ".jpg");
                                                                    try {
                                                                        friendPhoto.createNewFile();
                                                                    } catch (Exception e) {
                                                                        Log.d(TAG, "New file not created for image");
                                                                    }
                                                                    //downloads the file to storage
                                                                    photoref.getFile(friendPhoto);

                                                                    //gets metadata of friend's photos
                                                                    final long timeTaken = (long) friendPhotoRef.child("TimeTaken").getValue();
                                                                    final long latitude = (long) friendPhotoRef.child("Latitude").getValue();
                                                                    final long longitude = (long) friendPhotoRef.child("Longitude").getValue();
                                                                    final long karma = (long) friendPhotoRef.child("Karma").getValue();
                                                                    final boolean released = (boolean) friendPhotoRef.child("Released").getValue();

                                                                    photoref.getBytes(FIVE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                                        @Override
                                                                        public void onSuccess(byte[] bytes) {
                                                                            Log.d(TAG, "Conversion to bitmap successful");
                                                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                            if (bitmap != null) {
                                                                                Log.d(TAG, "Bitmap not null");
                                                                                RemotePhoto friendPhoto = new RemotePhoto(
                                                                                        bitmap,
                                                                                        (int) karma,
                                                                                        latitude,
                                                                                        longitude,
                                                                                        timeTaken,
                                                                                        released,
                                                                                        friendPhotoRef.getKey());
                                                                                if (friendPhoto != null) {
                                                                                    Log.d(TAG, "Friend photo added to cycle");
                                                                                    displayCycle.addToCycle(friendPhoto);
                                                                                }
                                                                            }

                                                                        }
                                                                    });
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {

                                                            }
                                                        });
                                                    }


                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Turning viewing friends off, resetting display..");
                            //Deletes friend album to sync with database
                            try{
                                FileUtils.deleteDirectory(new File(Environment.getExternalStorageDirectory() + "/DejaPhotoFriends"));
                            }
                            catch(Exception e)
                            {
                                Log.d(TAG, "Error when deleting directory");
                            }
                            PhotoLoader photoLoader = new DejaPhotoLoader();
                            displayCycle = new DisplayCycleMediator(photoLoader.getPhotosAsArray(PhotoService.this));
                        }




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
        filter.addAction("EDIT_LOCATION");

        receiver = new MyReceiver();
        registerReceiver(receiver, filter);

        /* Initializes DisplayCycleMediator with photos from the system */

        PhotoLoader photoLoader = new DejaPhotoLoader();
        //DejaPhotoDownloader downloader = new DejaPhotoDownloader(context);
        //ArrayList<DejaPhoto> allPhotos = downloader.downloadAllPhotos();
        //DejaPhoto[] allPhotosArray = new DejaPhoto[allPhotos.size()];
        //displayCycle = new DisplayCycleMediator(allPhotos.toArray(allPhotosArray));
        displayCycle = new DisplayCycleMediator(photoLoader.getPhotosAsArray(this));
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

            Bitmap photoBitmap;
            if ( dejaPhoto instanceof LocalPhoto ) {
                photoBitmap = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(),
                        ((LocalPhoto) dejaPhoto).getPhotoUri());
            }
            else {
                photoBitmap = ((RemotePhoto) dejaPhoto).getBitmap();
            }

            background.setBitmap(bitmapUtil.backgroundImage(photoBitmap, dejaPhoto.getLocation(),
                    dejaPhoto.getCustomLocation(), dejaPhoto.getKarma()));

            Log.d(TAG, "Displaying Previous DejaPhoto: " + dejaPhoto.getUniqueID()
                    + " (" + dejaPhoto.getScore() + ")");
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No DejaPhoto could be retrieved");
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

            Bitmap photoBitmap;
            if ( dejaPhoto instanceof LocalPhoto ) {
                photoBitmap = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(),
                        ((LocalPhoto) dejaPhoto).getPhotoUri());
            }
            else {
                photoBitmap = ((RemotePhoto) dejaPhoto).getBitmap();
            }

            background.setBitmap(bitmapUtil.backgroundImage(photoBitmap, dejaPhoto.getLocation(),
                    dejaPhoto.getCustomLocation(), dejaPhoto.getKarma()));

            Log.d(TAG, "Displaying Next DejaPhoto: " + dejaPhoto.getUniqueID()
                    + " (" + dejaPhoto.getScore() + ")");
        }

        catch (NullPointerException e) {
            Log.d(TAG, "No DejaPhoto could be retrieved");
        }

        catch (IllegalStateException e) {
            Log.d(TAG, "Uri not longer exists - this photo was deleted.");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * This method delegates release functionality to the ReleaseSingleUser, passing along the
     * DisplayCycleMediator and SharedPreferences.
     */
    public void releasePhoto() {

        /* If photo was successfully released, cycle forward */
        if ( releaseController.releasePhoto(currDisplayedPhoto) == 1 ) {
            cycleForward();
        }

    }


   /*
    * This method gives karma to the currently displayed photo, then enters a new record in the
    * database.
    */
   public void givePhotoKarma() {

       if ( currDisplayedPhoto != null && !(currDisplayedPhoto.hasKarma()) ) {
           Log.d(TAG, "Setting karma on currently displayed photo");
           currDisplayedPhoto.addKarma();
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
           //Unique DejaPhoto id given to a photo that has been given karma

           String photoid = "K_" + currDisplayedPhoto.getUniqueID();

           //stores unique photo id
           editor.putBoolean(photoid, true);
           editor.apply();

           Log.d(TAG, "Photoid is: " + photoid);
       }

       else {
           Log.d(TAG, "No reference to currently displayed photo - cannot set karma, or photo already has karma");
       }
   }
}
