package com.team29.cse110.team29dejaphoto.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.team29.cse110.team29dejaphoto.services.PhotoService;
import com.team29.cse110.team29dejaphoto.R;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    public static SharedPreferences dejaPreferences; // Holds the reference to the SharedPreferences file
    public static final String DEJA_PREFS = "Deja_Preferences"; // SharedPreference file key
    public static final String IsAppRunning = "IsAppRunning"; // App running key

    /* Declaration of xml UI Design TextViews */
    TextView appOnOffText;
    Button photoPickerButton;
    DrawerLayout dejaDrawer;
    ActionBarDrawerToggle dejaDrawerToggle;
    NavigationView navigationView;
    ImageView imageView;

    /* Declaration of xml UI Design Switches */
    Switch appOnOff;

    /* Declaration of the listeners */
    CompoundButton.OnCheckedChangeListener appOnOffSwitchListener;

    /* Declaration of xml UI Design Radio */
    RadioGroup radio;

    private final int PERMISSIONS_REQUEST_MEDIA = 1; // int value for permission to access MEDIA
    private final int PERMISSIONS_LOCATION = 2;      // int value for permission to access locationSwitch
    private final int PERMISSIONS_REQUEST_ALL = 3;   // int value for both permissions combined

    /* Called when the APP is first created */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dejaDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        dejaDrawerToggle = new ActionBarDrawerToggle(this, dejaDrawer,
                R.string.drawer_opened, R.string.drawer_closed);

        dejaDrawer.addDrawerListener(dejaDrawerToggle);
        dejaDrawerToggle.syncState();

        //creates drawer actionbar button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //sets title of actionbar
        getSupportActionBar().setTitle("LocalPhoto");

        navigationView = (NavigationView) findViewById(R.id.navi_view);

        //navigation drawer menu Listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        dejaDrawer.closeDrawers();
                        Log.d("drawer", item.getTitle() + " selected");
                        //Settings button was selected
                        if (item.getTitle().toString().equalsIgnoreCase("Settings")) {
                            //launch SettingsActivity
                            Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intentSettings);
                        }

                        if (item.getTitle().toString().equalsIgnoreCase("Add Friends")) {
                            Intent intentFriend = new Intent(MainActivity.this, FriendActivity.class);
                            startActivity(intentFriend);
                        }

                        if (item.getTitle().toString().equalsIgnoreCase("Log in")) {
                            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intentLogin);
                        }

                        if (item.getTitle().toString().equalsIgnoreCase("Camera")) {
                            Intent intentCamera = new Intent(MainActivity.this, CameraActivity.class);
                            startActivity(intentCamera);
                        }

                        return true;
                    }
                }
        );

        dejaPreferences = getSharedPreferences(DEJA_PREFS, 0); // Instantiate the shared preferences file

        /* Find the ID's for the UI TextViews to be displayed */

        appOnOffText = (TextView) findViewById(R.id.appSwitch);

        /* Find the ID's for the UI Designs to be used and linked to onClicks, listeners, etc */
        appOnOff = (Switch) findViewById(R.id.serviceButton);

        photoPickerButton = (Button) findViewById(R.id.photo_picker_button);

        imageView = (ImageView) findViewById(R.id.imageView);

        /*
         * Linker initialization for the switches, toggling if they can be clicked, if they are
         * checked, and updating shared preferences so that the user's preferences are saved
         * when the close and open the app
         */
        appOnOffSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "app is ON");
                    appOnOffText.setText("LocalPhoto is running!");
                    toggleSetting(IsAppRunning, true);
                    starter();
                } else {
                    Log.d(TAG, "app is OFF");
                    appOnOffText.setText("LocalPhoto is not running!");
                    Log.d(TAG, "Dejaphoto disable called");
                    toggleSetting(IsAppRunning, false);
                    stopper();
                }
            }
        };

        /* Linking listeners to switches */
        appOnOff.setOnCheckedChangeListener(appOnOffSwitchListener);

        boolean appRunCheck = dejaPreferences.contains(IsAppRunning); // Has the app started yet

        if (appRunCheck) {
            appOnOff.setChecked(true);
            appOnOffText.setText("LocalPhoto is running!");
        } else {
            Log.d(TAG, "app is disabled on startup");
            appOnOff.setChecked(false);
        }
    }

    /* Permissions Handling */

    public boolean checkPermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            return false;
        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            return false;
        }

        return true;
    }

    public void requestAllPermissions() {

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                },
                PERMISSIONS_REQUEST_ALL);
    }

    /* Method to decide what to do if a permission is allowed or denied */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_MEDIA : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                else {
                    Toast.makeText(this, "Error setting permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            case PERMISSIONS_LOCATION : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                else {
                    Toast.makeText(this, "Error setting permissions", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            case PERMISSIONS_REQUEST_ALL : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    starter();
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

    /* Start/Stop Service Toggle Listeners */

    /* Method to call onStartCommand when the user turns on the app */
    public void starter() {
        Log.d(TAG, "Starter button pushed");

        /* Permission was granted so the service can be started */
        if(checkPermissions()) {
            Intent intent = new Intent(MainActivity.this, PhotoService.class);
            startService(intent);

        } else {
            requestAllPermissions();
        }

        createAlbums(); // Create the new albums to store different photos
    }

    /* Method to call onDestroy when the user turns off the app */
    public void stopper() {
        Log.d(TAG, "Stopper button pushed");
        Intent intent = new Intent(MainActivity.this, PhotoService.class);
        dejaPreferences.edit().remove(IsAppRunning).apply();
        stopService(intent);
    }

    /*
     * Method to toggle boolean values stored in SharedPreferences. The settingName parameter is the
     * key value for the boolean you want to change.
     */
    private void toggleSetting(String settingName, boolean onOff) {

        SharedPreferences.Editor editor  = dejaPreferences.edit();

        boolean setting = dejaPreferences.getBoolean(settingName, true);
        Log.d(TAG, "" + setting);

        editor.putBoolean(settingName, onOff);

        if ( editor.commit() ) {
            Log.d(TAG, "Successfully changed " + settingName + " setting in sharedPreferences");
        }
        else {
            Log.d(TAG, "Could not change " + settingName + " setting in sharedPreferences");
        }
    }

    /**
     * Method that sends the intent so that a grid-view screen opens up that allows the user to
     * select 1 or more photos from their other albums and copy them into an album named
     * DejaPhotoCopied.
     *
     * @param view
     */
    public void photoPicker (View view){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");

        // Allows user to select more than 1 photo at a time
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


        /*File DejaPhotoCopied = new File(destinationPath);
        if (!DejaPhotoCopied.exists()){
            DejaPhotoCopied.mkdirs();
            Log.d(TAG, "directory made");
        }*/

        Uri photoPickerUri; // Holds the content Uri for a photo
        //FileOutputStream fos;
        //Bitmap finalBitmap = null;

        File sourceFile; // Holds the original photo we are copying
        File destinationFile; // Holds the copied photo

        Log.d("TAG", "Running onActivityResult");
        if (requestCode == 1 && resultCode == RESULT_OK && data != null /*&& data.getData() != null*/) {

            /*Uri uri1 = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri1);
                // Log.d(TAG, String.valueOf(bitmap));

                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            String trueUri;
            //imagesEncodedList = new ArrayList<String>();
            if (data.getData() != null) {

                photoPickerUri = data.getData();

                /*// Get the cursor
                Cursor cursor = getContentResolver().query(photoPickerUri,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imageEncoded = cursor.getString(columnIndex);
                editor.putString(photoPickerUri.toString(), imageEncoded);
                Log.d("TAG", "successfully wrote" + photoPickerUri.toString() + " to sharedpref");

                cursor.close();*/

                // Get correct Uri of a photo
                trueUri = getFileNameByUri(this, photoPickerUri);
                Log.d(TAG, "old uri: " + photoPickerUri);
                Log.d(TAG, "Found real uri of image: " + trueUri);

                //outputFile = new File(DejaPhotoCopied, trueUri);

                // Access the current directory of the image we want
                try{
                    //String filePath = DejaPhotoCopied.toString() + trueUri;
                    /*File temp = new File(DejaPhotoCopied, trueUri);
                    if(temp.exists()) {
                        Log.d(TAG, "HELP");
                    }*/

                    sourceFile = new File(/*Environment.getExternalStorageDirectory()
                            + "/DCIM/Camera/"
                            + trueUri.substring(trueUri.lastIndexOf('/') + 1)*/
                            trueUri
                            );

                    Log.d(TAG,"sourceFile is " + sourceFile);
                    //Uri uri = Uri.fromFile(file);

//                    finalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(sourceFile));
//                    fos = new FileOutputStream(destinationPath + trueUri.substring(trueUri.lastIndexOf('/')));
//
//                    //Log.d(TAG, "finalBitmap is " + String.valueOf(finalBitmap));
//                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
//                    fos.flush();
//                    fos.close();

                    // Copy the photo over to the new directory and update shared preferences
                    destinationFile = new File(destinationPath + trueUri.substring(trueUri.lastIndexOf('/')));
                    Log.d(TAG,"destinationFile is " +destinationFile);
                    FileUtils.copyFile(sourceFile, destinationFile);
                    MediaScannerConnection.scanFile(this, new String[] { destinationFile.getPath() }, null, null);
                    editor.putBoolean(Uri.fromFile(destinationFile).toString(), false);
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

                    trueUri = getFileNameByUri(this, photoPickerUri);
                    Log.d(TAG, "Found real uri of image: " + trueUri);

                    try{

                        // Access the current directory of the photo we want to copy
                        sourceFile = new File(Environment.getExternalStorageDirectory()
                                + "/DCIM/Camera/"
                                + trueUri.substring(trueUri.lastIndexOf('/') + 1)
                        );

                        Log.d(TAG,"sourceFile is " +sourceFile);
                        //Uri uri = Uri.fromFile(file);

                        /*finalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(sourceFile));
                        fos = new FileOutputStream(destinationPath + trueUri.substring(trueUri.lastIndexOf('/')));

                        //Log.d(TAG, "finalBitmap is " + String.valueOf(finalBitmap));
                        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.flush();
                        fos.close();*/

                        // Copy the photo over to the new directory and update shared preferences
                        destinationFile = new File(destinationPath + trueUri.substring(trueUri.lastIndexOf('/')));
                        Log.d(TAG,"destinationFile is " +destinationFile);
                        FileUtils.copyFile(sourceFile, destinationFile);
                        MediaScannerConnection.scanFile(this, new String[] { destinationFile.getPath() }, null, null);
                        editor.putBoolean(Uri.fromFile(destinationFile).toString(), false);

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
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        dejaDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dejaDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(dejaDrawerToggle.onOptionsItemSelected( item ))
            return true;


        return super.onOptionsItemSelected(item);
    }

    /**
     * Method to get the correct uri data from a photo
     */
    public String getFileNameByUri(Context context, Uri uri) {

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
            filePath = /*"file://" +*/ cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * Method to create the 3 new albums for storing specific types of photos separately
     */
    public void createAlbums() {

        // Initialize the paths to where the 3 new folders in the SD card will be
        String path1 = Environment.getExternalStorageDirectory() + "/DejaPhotoCopied";
        String path2 = Environment.getExternalStorageDirectory() + "/DejaPhotoFriends";
        String path3 = Environment.getExternalStorageDirectory() + "/DejaPhoto";

        // Initialize the 3 new folders in their respective paths
        File DejaPhotoCopied = new File(path1);
        File DejaPhotoFriends = new File(path2);
        File DejaPhoto = new File(path3);

        // Create the DejaPhotoCopied album if it doesn't already exist
        if (!DejaPhotoCopied.exists()){
            DejaPhotoCopied.mkdirs();
            Log.d(TAG, "DejaPhotoCopied directory created");
        }

        // Create the DejaPhotoFriends album if it doesn't already exist
        if (!DejaPhotoFriends.exists()){
            DejaPhotoFriends.mkdirs();
            Log.d(TAG, "DejaPhotoFriends directory created");
        }

        // Create the DejaPhoto album if it doesn't already exist
        if (!DejaPhoto.exists()){
            DejaPhoto.mkdirs();
            Log.d(TAG, "DejaPhoto directory created");
        }
    }
}
