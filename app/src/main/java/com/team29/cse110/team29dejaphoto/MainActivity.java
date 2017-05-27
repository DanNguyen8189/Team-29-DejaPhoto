package com.team29.cse110.team29dejaphoto;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    public static SharedPreferences dejaPreferences; // Holds the reference to the SharedPreferences file
    public static final String DEJA_PREFS = "Deja_Preferences"; // SharedPreference file key
    public static final String IsAppRunning = "IsAppRunning"; // App running key
    public static final String IsDejaVuModeOn = "IsDejaVuModeOn"; // DejaVu mode key
    public static final String IsLocationOn = "IsLocationOn"; // Location key
    public static final String IsTimeOn = "IsTimeOn"; // Time key
    public static final String IsDateOn = "IsDateOn"; // Date key
    public static final String UpdateInterval = "UpdateInterval"; // Update interval key


    boolean useDefaultGallery = true; // By default use the user's custom default album

    /* Declaration of xml UI Design TextViews */
    TextView appOnOffText;
    TextView dejavuText;
    TextView locationText;
    TextView timeText;
    TextView dateText;
    TextView updateIntervalText;
    TextView updateIntervalNumber;
    DrawerLayout dejaDrawer;
    ActionBarDrawerToggle dejaDrawerToggle;
    NavigationView navigationView;

    /* Declaration of xml UI Design Switches */
    Switch appOnOff;
    Switch dejavu;
    Switch location;
    Switch time;
    Switch date;

    /* Declaration of xml UI Design SeekBar */
    SeekBar upDateInterval;

    /* Declaration of the listeners */
    CompoundButton.OnCheckedChangeListener appOnOffSwitchListener;
    CompoundButton.OnCheckedChangeListener dejavuSwitchListener;
    CompoundButton.OnCheckedChangeListener locationSwitchListener;
    CompoundButton.OnCheckedChangeListener timeSwitchListener;
    CompoundButton.OnCheckedChangeListener dateSwitchListener;
    SeekBar.OnSeekBarChangeListener timeInterValListener;

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
        dejaDrawerToggle = new ActionBarDrawerToggle(this,dejaDrawer,
                R.string.drawer_opened,R.string.drawer_closed);

        dejaDrawer.addDrawerListener(dejaDrawerToggle);
        dejaDrawerToggle.syncState();

        //creates drawer actionbar button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //sets title of actionbar
        getSupportActionBar().setTitle("DejaPhoto");

        navigationView = (NavigationView) findViewById(R.id.navi_view);

        //navigation drawer menu Listener
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        dejaDrawer.closeDrawers();
                        Log.d("drawer", item.getTitle() + " selected");
                        //Settings button was selected
                        if(item.getTitle().toString().equalsIgnoreCase("Settings"))
                        {
                            //launch SettingsActivity
                            Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                            startActivity(intentSettings);
                        }
                        return true;
                    }
                }
        );





        dejaPreferences = getSharedPreferences(DEJA_PREFS, 0); // Instantiate the shared preferences file

        /* Find the ID's for the UI TextViews to be displayed */

        appOnOffText = (TextView) findViewById(R.id.appSwitch);
        dejavuText = (TextView) findViewById(R.id.dejavu_mode_text);
        locationText = (TextView) findViewById(R.id.location_text);
        timeText = (TextView) findViewById(R.id.time_text);
        dateText = (TextView) findViewById(R.id.date_text);
        updateIntervalText = (TextView) findViewById(R.id.update_interval_text);
        updateIntervalNumber = (TextView) findViewById(R.id.update_interval_number);

        /* Find the ID's for the UI Designs to be used and linked to onClicks, listeners, etc */
        appOnOff = (Switch) findViewById(R.id.serviceButton);
        dejavu = (Switch) findViewById(R.id.dejavu_mode_switch);
        location = (Switch) findViewById(R.id.location_switch);
        time = (Switch) findViewById(R.id.time_switch);
        date = (Switch) findViewById(R.id.date_switch);
        radio = (RadioGroup) findViewById(R.id.RadioGroup);
        upDateInterval = (SeekBar) findViewById(R.id.update_interval_bar);

        /* Linker initialization for the switches, toggling if they can be clicked, if they are
         * checked, and updating shared preferences so that the user's preferences are saved
         * when the close and open the app
         */

        appOnOffSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "app is ON");
                    appOnOffText.setText("DejaPhoto is running!");
                    toggleSetting(IsAppRunning, true);
                    /*dejavuSwitch.setClickable(true);
                    locationSwitch.setClickable(true);
                    timeSwitch.setClickable(true);
                    dateSwitch.setClickable(true);*/
                    changeUIOpacities(1);
                    starter();
                }

                else {
                    Log.d(TAG, "app is OFF");
                    appOnOffText.setText("DejaPhoto is not running!");
                    Log.d(TAG, "Dejaphoto disable called");
                    toggleSetting(IsAppRunning, false);
                    //dejavuSwitch.setChecked(false);
                    /*dejavuSwitch.setClickable(false);
                    locationSwitch.setClickable(false);
                    timeSwitch.setClickable(false);
                    dateSwitch.setClickable(false); */
                    changeUIOpacities(.7f);
                    stopper();
                }
            }
        };

        dejavuSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dejavuText.setText("DejaVu mode enabled");
                    location.setChecked(true);
                    time.setChecked(true);
                    date.setChecked(true);
                    location.setClickable(true);
                    time.setClickable(true);
                    date.setClickable(true);
                    toggleSetting(IsDejaVuModeOn, true);
                }

                else {
                    dejavuText.setText("DejaVu mode disabled");
                    location.setChecked(false);
                    time.setChecked(false);
                    date.setChecked(false);
                    location.setClickable(false);
                    time.setClickable(false);
                    date.setClickable(false);
                    toggleSetting(IsDejaVuModeOn, false);
                }
            }
        };

        locationSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    locationText.setText("Location enabled");
                    toggleSetting(IsLocationOn, true);
                }

                else {
                    locationText.setText("Location disabled");
                    toggleSetting(IsLocationOn, false);
                }
            }
        };

        timeSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timeText.setText("Time enabled");
                    toggleSetting(IsTimeOn, true);
                }

                else {
                    timeText.setText("Time disabled");
                    toggleSetting(IsTimeOn, false);
                }
            }
        };

        dateSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dateText.setText("Date enabled");
                    toggleSetting(IsDateOn, true);
                }

                else {
                    dateText.setText("Date disabled");
                    toggleSetting(IsDateOn, false);
                }
            }
        };

        /* Listener for the seek bar to allow user to set their own interval */
        timeInterValListener = new SeekBar.OnSeekBarChangeListener() {

            /* Change the text as the slider changes */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateIntervalNumber.setText((progress + 1)/60 + " hours " + (progress + 1)%60 + " minutes");
            }

            /* Method needs to be overridden */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            /* Update shared preferences file once the user is done sliding */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor  = dejaPreferences.edit();
                editor.putInt(UpdateInterval, (seekBar.getProgress() + 1)*60000);
                editor.apply();
            }
        };

        /* Linking listeners to switches */
        appOnOff.setOnCheckedChangeListener(appOnOffSwitchListener);
        dejavu.setOnCheckedChangeListener(dejavuSwitchListener);
        location.setOnCheckedChangeListener(locationSwitchListener);
        time.setOnCheckedChangeListener(timeSwitchListener);
        date.setOnCheckedChangeListener(dateSwitchListener);
        upDateInterval.setOnSeekBarChangeListener(timeInterValListener);

        boolean appRunCheck = dejaPreferences.contains(IsAppRunning); // Has the app started yet

        /* Check if app is supposed to be running */
        /*
        if(appRunCheck) {
                appOnOff.setChecked(true);
                appOnOffText.setText("DejaPhoto is enabled");


            if(dejaPreferences.getBoolean(IsDejaVuModeOn, true)) {
                dejavuSwitch.setOnCheckedChangeListener(null);
                dejavuSwitch.setChecked(true);
                dejavuSwitch.setOnCheckedChangeListener(dejavuSwitchListener);


                if(dejaPreferences.getBoolean(IsLocationOn, true)) {
                    locationSwitch.setOnCheckedChangeListener(null);
                    locationSwitch.setChecked(true);
                    locationSwitch.setOnCheckedChangeListener(locationSwitchListener);
                }

                else {
                    locationSwitch.setChecked(true);
                    locationSwitch.setChecked(false);
                }


                if(dejaPreferences.getBoolean(IsTimeOn, true)) {
                    timeSwitch.setOnCheckedChangeListener(null);
                    timeSwitch.setChecked(true);
                    timeSwitch.setOnCheckedChangeListener(timeSwitchListener);
                }

                else {
                    timeSwitch.setChecked(true);
                    timeSwitch.setChecked(false);
                }


                if(dejaPreferences.getBoolean(IsDateOn, true)) {
                    dateSwitch.setOnCheckedChangeListener(null);
                    dateSwitch.setChecked(true);
                    dateSwitch.setOnCheckedChangeListener(dateSwitchListener);
                }

                else {
                    dateSwitch.setChecked(true);
                    dateSwitch.setChecked(false);
                }
            }

            else {
                dejavuSwitch.setChecked(true);
                dejavuSwitch.setChecked(false);
            }


            updateIntervalBar.setProgress(dejaPreferences.getInt(UpdateInterval, 300000)/60000 - 1);
        }

        else {
            Log.d(TAG, "disable app");
            appOnOff.setChecked(true);
            appOnOff.setChecked(false);
            dejavuSwitch.setChecked(true);
            dejavuSwitch.setChecked(false);
        }
        */


        /***************************************************************************************/

        if(appRunCheck) {
            appOnOff.setChecked(true);
            appOnOffText.setText("DejaPhoto is running!");
        }
        else{
            Log.d(TAG, "app is disabled on startup");
            appOnOff.setChecked(false);
        }

        if(dejaPreferences.getBoolean(IsDejaVuModeOn, true)) {
            dejavu.setOnCheckedChangeListener(null);
            dejavu.setChecked(true);
            dejavu.setOnCheckedChangeListener(dejavuSwitchListener);

                /* Check if user has locationSwitch enabled */
            if(dejaPreferences.getBoolean(IsLocationOn, true)) {
                location.setOnCheckedChangeListener(null);
                location.setChecked(true);
                location.setOnCheckedChangeListener(locationSwitchListener);
            }

            else {
                location.setChecked(true);
                location.setChecked(false);
            }

                /* Check if user has locationSwitch on */
            if(dejaPreferences.getBoolean(IsTimeOn, true)) {
                time.setOnCheckedChangeListener(null);
                time.setChecked(true);
                time.setOnCheckedChangeListener(timeSwitchListener);
            }

            else {
                time.setChecked(true);
                time.setChecked(false);
            }

                /* Check if user has dateSwitch on */
            if(dejaPreferences.getBoolean(IsDateOn, true)) {
                date.setOnCheckedChangeListener(null);
                date.setChecked(true);
                date.setOnCheckedChangeListener(dateSwitchListener);
            }

            else {
                date.setChecked(true);
                date.setChecked(false);
            }
        }

        else {
            dejavu.setChecked(true);
            dejavu.setChecked(false);
        }

        /* Check what update interval the user has set */
        upDateInterval.setProgress(dejaPreferences.getInt(UpdateInterval, 300000)/60000 - 1);
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
                        Manifest.permission.ACCESS_FINE_LOCATION
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
    }

    /* Method to call onDestroy when the user turns off the app */
    public void stopper() {
        Log.d(TAG, "Stopper button pushed");
        Intent intent = new Intent(MainActivity.this, PhotoService.class);
        dejaPreferences.edit().remove(IsAppRunning).apply();
        stopService(intent);
    }


    /* Method to display the onClick for which gallery to use, only one for now */
    public void onClickRadioButton(View view) {

        switch(view.getId()) {

            case R.id.DefaultAlbum:
                if (useDefaultGallery) {
                    break;
                }

                else {
                    useDefaultGallery = !useDefaultGallery;
                    break;
                }

            case R.id.DejaAlbum:
                if (useDefaultGallery) {
                    useDefaultGallery = !useDefaultGallery;
                    break;
                }

                else {
                    break;
                }
        }
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

    private void changeUIOpacities(float num){
        dejavu.setAlpha(num);
        location.setAlpha(num);
        time.setAlpha(num);
        date.setAlpha(num);
        upDateInterval.setAlpha(num);
        dejavuText.setAlpha(num);
        locationText.setAlpha(num);
        timeText.setAlpha(num);
        dateText.setAlpha(num);
        updateIntervalText.setAlpha(num);
        updateIntervalNumber.setAlpha(num);
    }
    /* To be done next milestone */
    public void onCreateCustomAlbum(View view) {

        Intent intent = new Intent(getApplicationContext(), CustomAlbumActivity.class);
        startActivity(intent);
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

}
