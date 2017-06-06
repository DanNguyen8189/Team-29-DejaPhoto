package com.team29.cse110.team29dejaphoto.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Wis on 6/4/2017.
 */

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private String tempPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File photoFile = null;
        Uri photoURI = null;

        try {
            photoFile = createImageFile();
            photoURI = FileProvider.getUriForFile(
                    this,
                    getApplicationContext()
                            .getPackageName() + ".provider", photoFile);

        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.i(TAG, "IOException");
        }

        // Continue only if the File was successfully created
        if (photoFile != null && photoURI != null) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            Log.d(TAG, "Starting image capture intent: " + photoURI);

            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Log.d(TAG, "PHOTO EXISTS: " + (new File(tempPath)).isFile());
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/DejaPhoto");
        if(!storageDir.exists() && !storageDir.mkdir())
            return null;

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        Log.d(TAG, "File created: " + image.getAbsolutePath());
        tempPath = image.getAbsolutePath();

        return image;
    }
}
