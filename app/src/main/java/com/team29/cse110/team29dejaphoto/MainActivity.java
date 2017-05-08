package com.team29.cse110.team29dejaphoto;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    private final String TAG = "MainActivity";

    WallpaperManager background;

    private DisplayCycle displayCycle = new DisplayCycle();


    Button loadPhotosButton; // click to load all photos
    ImageButton buttonLeft;  // click to cycle back
    ImageButton buttonRight; // click to cycle forward

    private final int PERMISSIONS_REQUEST_MEDIA = 1; // int value for permission to access MEDIA
    private final int PERMISSIONS_NEXT_WALLPAPER = 2;     // int value for permission to change to the next wallpaper
    private final int PERMISSIONS_PREV_WALLPAPER = 3; // int value for permission to change to the previous wallpaper

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPhotosButton = (Button) findViewById(R.id.loadPhotos);
        buttonLeft = (ImageButton) findViewById(R.id.leftArrow);
        buttonRight = (ImageButton) findViewById(R.id.rightArrow);

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
    /*
     * This is the onClick method for the load images button.
     */
    public void loadPhotos(View view) {

        loadPhotosButton.setEnabled(false);  /* Disable button immediately so user cannot
                                                repeatedly load all photos */

        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_MEDIA);

    }

    public void changeWallpaper(View view) {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SET_WALLPAPER}, PERMISSIONS_NEXT_WALLPAPER);
    }

    public void changeWallpaper2(View view) {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SET_WALLPAPER}, PERMISSIONS_PREV_WALLPAPER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch ( requestCode ) {
            case PERMISSIONS_REQUEST_MEDIA : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    DejaPhoto[] gallery = getPhotosAsArray();
                    fillDisplayCycle(gallery);
                    Toast.makeText(this, "Done Loading Photos", Toast.LENGTH_SHORT).show();
                    return;

                }
                else {
                    Toast.makeText(this, "Error setting permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            case PERMISSIONS_NEXT_WALLPAPER : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Setting Next Wallpaper", Toast.LENGTH_SHORT).show();
                    cycleForward();
                    return;
                }
                else {
                    Toast.makeText(this, "Error setting permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            case PERMISSIONS_PREV_WALLPAPER : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Setting Prev Wallpaper", Toast.LENGTH_SHORT).show();
                    cycleBack();
                    return;
                }
                else {
                    Toast.makeText(this, "Error setting permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            default: {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private DejaPhoto[] getPhotosAsArray() {

        Log.d(TAG, "Entering getPhotosAsArray method");
        String[] projection = { MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.LATITUDE,
                MediaStore.Images.Media.LONGITUDE,
                MediaStore.Images.Media.DATE_ADDED };

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);

        int numOfPhotos = cursor.getCount();
        if (numOfPhotos == 0 || numOfPhotos == 1) {
            cursor.close();
            return null;
        }
        DejaPhoto[] gallery = new DejaPhoto[numOfPhotos];
        Log.d(TAG, "Retrieved " + numOfPhotos + " photos");

        int titleIndex = cursor.getColumnIndex(MediaStore.Images.Media.TITLE);
        int latIndex = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
        int longIndex = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
        int timeIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
        int count = 0;

        while (cursor.moveToNext()) {

            String filename = cursor.getString(titleIndex) + ".jpg";
            String absolutePath = Environment.getExternalStorageDirectory() + "/DCIM/CAMERA/" + filename;
            File file = new File(absolutePath);
            Uri uri = Uri.fromFile(file);

            Log.d(TAG, filename);
            gallery[count] = new DejaPhoto(uri,
                    cursor.getDouble(latIndex),
                    cursor.getDouble(longIndex),
                    cursor.getLong(timeIndex));

            count++;

        }
        cursor.close();

        return gallery;

    }

    private void fillDisplayCycle(DejaPhoto[] gallery) {

        if (gallery != null) {
            for (int i = 0; i < gallery.length; i++) {
                displayCycle.addToCycle(gallery[i]);
            }
        }

    }

}
