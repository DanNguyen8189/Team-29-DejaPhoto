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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    WallpaperManager background;

    private DisplayCycle displayCycle = new DisplayCycle();


    Button loadPhotosButton; // click to load all photos
    ImageButton buttonLeft; // click to cycle back
    ImageButton buttonRight; // click to cycle forward

    private final int PERMISSIONS_REQUEST_MEDIA = 1; // int value for permission to access MEDIA

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPhotosButton = (Button) findViewById(R.id.loadPhotos);
        buttonLeft = (ImageButton) findViewById(R.id.leftArrow);
        buttonRight = (ImageButton) findViewById(R.id.rightArrow);

    }


    public void cycleBack(View view) {
        background = WallpaperManager.getInstance(getApplicationContext());
        try {
            background.setBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), displayCycle.getPrevPhoto().getPhotoUri()));
        }

        catch (Exception e) {
        }
    }

    public void cycleForward(View view) {
        background = WallpaperManager.getInstance(getApplicationContext());
        try {
            background.setBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), displayCycle.getNextPhoto().getPhotoUri()));
        }

        catch (Exception e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch ( requestCode ) {
            case PERMISSIONS_REQUEST_MEDIA : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    AsyncLoadImages imageLoader = new AsyncLoadImages();
                    imageLoader.execute();
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


    public void loadPhotos(View view) {

        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_MEDIA);

    }


    private class AsyncLoadImages extends AsyncTask<DisplayCycle, String, DejaPhoto[]> {

        ProgressDialog progressDialog;

        @Override
        protected DejaPhoto[] doInBackground(DisplayCycle... params) {
            return getPhotosAsArray();
        }

        @Override
        protected void onPostExecute(DejaPhoto[] result) {

            if (result != null) {
                for (int i = 0; i < result.length; i++) {
                    displayCycle.addToCycle(result[i]);
                }
            }

            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this,
                    "ProgressDialog",
                    "Loading Photos...");
        }

        @Override
        protected void onProgressUpdate(String... text) {
        }

        private DejaPhoto[] getPhotosAsArray() {

            String[] projection = { MediaStore.Images.Media.TITLE,
                    MediaStore.Images.Media.LATITUDE,
                    MediaStore.Images.Media.LONGITUDE,
                    MediaStore.Images.Media.DATE_ADDED };

            String title = "";
            double latitude = 0;
            double longitude = 0;
            long time = 0L;

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

            int titleIndex = cursor.getColumnIndex(MediaStore.Images.Media.TITLE);
            int latIndex = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
            int longIndex = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
            int timeIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            int count = 0;

            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {

                    title = cursor.getString(titleIndex);
                    latitude = cursor.getDouble(latIndex);
                    longitude = cursor.getDouble(longIndex);
                    time = cursor.getLong(timeIndex);

                    String filename = title + ".jpg";
                    String absolutePath = Environment.getExternalStorageDirectory() + "/DCIM/CAMERA/" + filename;
                    File file = new File(absolutePath);
                    Uri uri = Uri.fromFile(file);

                    gallery[count] = new DejaPhoto(uri, latitude, longitude, time, null);
                    count++;

                }
            }

            cursor.close();
            return gallery;

        }

    }

}
