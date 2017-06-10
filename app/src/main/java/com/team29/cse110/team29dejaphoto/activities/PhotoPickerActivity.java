package com.team29.cse110.team29dejaphoto.activities;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.team29.cse110.team29dejaphoto.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PhotoPickerActivity extends AppCompatActivity {
    private ImageView photoView;

    private final String TAG = "PhotoPickerActivity";

    public static SharedPreferences dejaPreferences; // Holds the reference to the SharedPreferences file
    public static final String DEJA_PREFS = "Deja_Preferences"; // SharedPreference file key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        photoView = (ImageView) findViewById(R.id.imageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        dejaPreferences = getSharedPreferences(DEJA_PREFS, 0); // Instantiate the shared preferences file

        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    /**
     * Callback for PhotoPicker Request
     * @param requestCode - The integer request code allowing the user to identify who this result came from
     * @param resultCode - The integer result code returned by the child activity
     * @param data - an intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Open the editor for the SharedPreferences file
        SharedPreferences.Editor editor = dejaPreferences.edit();

        /* TODO probably delete this line */
        String path = Environment.getExternalStorageDirectory() + "/DejaPhotoCopied";

        // Initialize the path to the SD card where the copied photos are stored using PhotoPicker
        String destinationPath = Environment.getExternalStorageDirectory() + "/DejaPhotoCopied";

        Uri photoPickerUri; // Holds the content Uri for a photo

        File sourceFile; // Holds the original photo we are copying
        File destinationFile; // Holds the copied photo

        Log.d("TAG", "Running onActivityResult");
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            String trueUri;

            if (data.getData() != null) {

                photoPickerUri = data.getData();

                // Get correct Uri of a photo
                trueUri = getFileNameByUri(photoPickerUri);
                Log.d(TAG, "old uri: " + photoPickerUri);
                Log.d(TAG, "Found real uri of image: " + trueUri);

                //outputFile = new File(DejaPhotoCopied, trueUri);

                // Access the current directory of the image we want
                try{
                    sourceFile = new File(trueUri);

                    Log.d(TAG,"sourceFile is " + sourceFile);

                    // Copy the photo over to the new directory and update shared preferences. the +dejacopied in the file path
                    // is added so that it can be differentiated from the original photo uduring loading
                    destinationFile = new File(destinationPath + "/dejaCopied" + trueUri.substring(trueUri.lastIndexOf('/')+1));
                    Log.d(TAG,"destinationFile is " +destinationFile);
                    FileUtils.copyFile(sourceFile, destinationFile);
                    MediaScannerConnection.scanFile(this, new String[] { destinationFile.getPath() }, null, null);

                    //put in sharedpreferences if not there already
                    if (!dejaPreferences.contains(Uri.fromFile(destinationFile).toString())) {
                        editor.putString(Uri.fromFile(destinationFile).toString(), "");
                    }

                    Log.d(TAG, "WE ARE WRITING THIS TO SHARED: " +Uri.fromFile(destinationFile).toString());

                }
                catch (FileNotFoundException e){
                    Toast.makeText(this, "There's a problem finding photo to sd card: File not found", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                catch (IOException e){
                    Toast.makeText(this, "There's a problem finding photo to sd card: IO exception", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            // More than one photo was selected
            else if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                Log.d("TAG", "Entered clipdata");
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    photoPickerUri = item.getUri();

                    // Get the cursor
                    Cursor cursor = getContentResolver().query(photoPickerUri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    trueUri = getFileNameByUri(photoPickerUri);
                    Log.d(TAG, "Found real uri of image: " + trueUri);

                    try{

                        // Access the current directory of the photo we want to copy
                        sourceFile = new File(Environment.getExternalStorageDirectory()
                                + "/DCIM/Camera/"
                                + trueUri.substring(trueUri.lastIndexOf('/') + 1)
                        );

                        Log.d(TAG,"sourceFile is " +sourceFile);

                        // Copy the photo over to the new directory and update shared preferences
                        destinationFile = new File(destinationPath + "/dejaCopied" + trueUri.substring(trueUri.lastIndexOf('/')+1));
                        Log.d(TAG,"destinationFile is " +destinationFile);
                        FileUtils.copyFile(sourceFile, destinationFile);
                        MediaScannerConnection.scanFile(this, new String[] { destinationFile.getPath() }, null, null);

                        //put in sharedpreferences if not there already
                        if (!dejaPreferences.contains(Uri.fromFile(destinationFile).toString())) {
                            editor.putString(Uri.fromFile(destinationFile).toString(), "");
                        }

                        Log.d(TAG, "WE ARE WRITING THIS TO SHARED: " +Uri.fromFile(destinationFile).toString());
                    }
                    catch (FileNotFoundException e){
                        Toast.makeText(this, "There's a problem finding photo to sd card: File not found", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    catch (IOException e){
                        Toast.makeText(this, "There's a problem finding photo to sd card: IO exception", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                    cursor.close();
                    editor.putBoolean(trueUri, false);
                }
            }
        }
        editor.commit();
        finish();
    }
    /**
     * Method to get the correct uri data from a photo
     */
    public String getFileNameByUri(Uri uri) {

        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
}
