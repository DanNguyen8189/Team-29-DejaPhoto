package com.team29.cse110.team29dejaphoto;

import android.Manifest;
import android.app.ProgressDialog;
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

    ImageButton arrowLeft; //left arrow button
    ImageButton arrowRight; //right arrow button
    Button loadPhotosButton; // click to load all photos

    private final int PERMISSIONS_REQUEST_MEDIA = 1; // int value for permission to access MEDIA

    private LoaderPackage loaderPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrowLeft = (ImageButton) findViewById(R.id.leftArrow);
        arrowRight = (ImageButton) findViewById(R.id.rightArrow);
        loadPhotosButton = (Button) findViewById(R.id.loadPhotos);

        /*
        TODO
        arrowLeft.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        }*/

    }

    public DejaPhoto[] getAllPhotosAsArray() {
        return loaderPackage.getGallery();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch ( requestCode ) {
            case PERMISSIONS_REQUEST_MEDIA : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    loaderPackage = new LoaderPackage();
                    AsyncLoadImages imageLoader = new AsyncLoadImages();
                    imageLoader.execute(loaderPackage);
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


    private void loadPhotos() {

        ActivityCompat.requestPermissions(this,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_MEDIA);

    }


    private class AsyncLoadImages extends AsyncTask<LoaderPackage, String, DejaPhoto[]> {

        @Override
        protected DejaPhoto[] doInBackground(LoaderPackage... params) {

            String title = "";
            double latitude = 0;
            double longitude = 0;
            long time = 0L;

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    params[0].getProjection(),
                    null,
                    null,
                    null);

            int numOfPhotos = cursor.getCount();
            params[0].initializeGallery(numOfPhotos);
            DejaPhoto[] gallery = params[0].getGallery();

            int titleIndex = cursor.getColumnIndex(MediaStore.Images.Media.TITLE);
            int latIndex = cursor.getColumnIndex(MediaStore.Images.Media.LATITUDE);
            int longIndex = cursor.getColumnIndex(MediaStore.Images.Media.LONGITUDE);
            int timeIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
            int count = 0;

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

            return gallery;
        }

        @Override
        protected void onPostExecute(DejaPhoto[] result) {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(String... text) {

        }

    }

}
