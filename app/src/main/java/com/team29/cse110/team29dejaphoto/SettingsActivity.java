package com.team29.cse110.team29dejaphoto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private final String TAG = "SettingsActivity";

    public static SharedPreferences dejaPreferences; // Holds the reference to the SharedPreferences file
    public static final String DEJA_PREFS = "Deja_Preferences"; // SharedPreference file key
    public static final String IsAppRunning = "IsAppRunning"; // App running key
    public static final String IsViewingOwn = "IsViewingOwn"; //viewing own photos key
    public static final String IsViewingFriends = "IsViewingFriends"; //viewing friends photos key
    public static final String IsSharingPhotos = "IsSharingPhotos"; //sharing own photos key
    public static final String IsDejaVuModeOn = "IsDejaVuModeOn"; // DejaVu mode key
    public static final String IsLocationOn = "IsLocationOn"; // Location key
    public static final String IsTimeOn = "IsTimeOn"; // Time key
    public static final String IsDateOn = "IsDateOn"; // Date key
    public static final String UpdateInterval = "UpdateInterval"; // Update interval key


    /* Declaration of xml UI Design TextViews */
    TextView whosePhotosText;
    TextView dejavuSettingsText;
    TextView dejavuModeText;
    TextView locationText;
    TextView timeText;
    TextView dateText;
    TextView updateIntervalText;
    TextView updateIntervalNumber;

    //TODO; what are these for?
    DrawerLayout dejaDrawer;
    NavigationView navigationView;

    /* Declaration of xml UI Design Switches */
    CheckBox yourPhotosBox;
    CheckBox friendPhotosBox;
    CheckBox sharePhotosBox;
    Switch dejavuSwitch;
    Switch locationSwitch;
    Switch timeSwitch;
    Switch dateSwitch;

    /* Declaration of xml UI Design SeekBar */
    SeekBar updateIntervalBar;

    /* Declaration of the listeners */
    CompoundButton.OnCheckedChangeListener yourPhotosBoxListener;
    CompoundButton.OnCheckedChangeListener friendPhotosBoxListener;
    CompoundButton.OnCheckedChangeListener sharePhotosBoxListener;
    CompoundButton.OnCheckedChangeListener dejavuSwitchListener;
    CompoundButton.OnCheckedChangeListener locationSwitchListener;
    CompoundButton.OnCheckedChangeListener timeSwitchListener;
    CompoundButton.OnCheckedChangeListener dateSwitchListener;
    SeekBar.OnSeekBarChangeListener updateIntervalBarListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //creates back button
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        //get the sharedpreferences file
        dejaPreferences = getSharedPreferences(DEJA_PREFS, 0);

        /* Find the ID's for the UI TextViews to be displayed */
        whosePhotosText = (TextView) findViewById(R.id.whose_photos_text);
        dejavuSettingsText = (TextView) findViewById(R.id.dejavu_settings_text);
        dejavuModeText = (TextView) findViewById(R.id.dejavu_mode_text);
        locationText = (TextView) findViewById(R.id.location_text);
        timeText = (TextView) findViewById(R.id.time_text);
        dateText = (TextView) findViewById(R.id.date_text);
        updateIntervalText = (TextView) findViewById(R.id.update_interval_text);
        updateIntervalNumber = (TextView) findViewById(R.id.update_interval_number);

        /* Find the ID's for the UI Designs to be used and linked to onClicks, listeners, etc */
        yourPhotosBox = (CheckBox) findViewById(R.id.your_photos_box);
        friendPhotosBox = (CheckBox) findViewById(R.id.friend_photos_box);
        sharePhotosBox = (CheckBox) findViewById(R.id.share_photos_box);
        dejavuSwitch = (Switch) findViewById(R.id.dejavu_mode_switch);
        locationSwitch = (Switch) findViewById(R.id.location_switch);
        timeSwitch = (Switch) findViewById(R.id.time_switch);
        dateSwitch = (Switch) findViewById(R.id.date_switch);
        updateIntervalBar = (SeekBar) findViewById(R.id.update_interval_bar);

        //TODO is this needed
        boolean appRunCheck = dejaPreferences.contains(IsAppRunning); // Has the app started yet

        /* Linker initialization for the switches, toggling if they can be clicked, if they are
         * checked, and updating shared preferences so that the user's preferences are saved
         * when the close and open the app
         */

        yourPhotosBoxListener = new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                toggleSetting(IsViewingOwn, isChecked);
            }
        };

        friendPhotosBoxListener = new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                toggleSetting(IsViewingFriends, isChecked);
            }
        };

        sharePhotosBoxListener = new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                toggleSetting(IsSharingPhotos, isChecked);
                /*if(isChecked)
                {
                    Log.d(TAG,"Sharing turned on");
                    shareStarter();
                }
                else
                {
                    Log.d(TAG,"Sharing turned off");
                    shareStopper();
                }*/

                //we only need this because service automatically destroys itself after finishing
                shareStarter( isChecked );
            }
        };

        dejavuSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                locationSwitch.setChecked(isChecked);
                timeSwitch.setChecked(isChecked);
                dateSwitch.setChecked(isChecked);
                locationSwitch.setClickable(isChecked);
                timeSwitch.setClickable(isChecked);
                dateSwitch.setClickable(isChecked);
                toggleSetting(IsDejaVuModeOn, isChecked);
                dejavuModeText.setText(newText("Dejavu Mode", isChecked));
            }
        };

        locationSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSetting(IsLocationOn, isChecked);
                locationText.setText(newText("Location", isChecked));
            }
        };

        timeSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSetting(IsTimeOn, isChecked);
                timeText.setText(newText("Time", isChecked));
            }
        };

        dateSwitchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSetting(IsDateOn, isChecked);
                dateText.setText(newText("Date", isChecked));
            }
        };

        /* Listener for the seek bar to allow user to set their own interval */
        updateIntervalBarListener = new SeekBar.OnSeekBarChangeListener() {

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

        /* Linking listeners */
        yourPhotosBox.setOnCheckedChangeListener(yourPhotosBoxListener);
        friendPhotosBox.setOnCheckedChangeListener(friendPhotosBoxListener);
        sharePhotosBox.setOnCheckedChangeListener(sharePhotosBoxListener);
        dejavuSwitch.setOnCheckedChangeListener(dejavuSwitchListener);
        locationSwitch.setOnCheckedChangeListener(locationSwitchListener);
        timeSwitch.setOnCheckedChangeListener(timeSwitchListener);
        dateSwitch.setOnCheckedChangeListener(dateSwitchListener);
        updateIntervalBar.setOnSeekBarChangeListener(updateIntervalBarListener);

        /* initialize settings to correct states on startup */
        yourPhotosBox.setChecked(dejaPreferences.getBoolean(IsViewingOwn, true));
        friendPhotosBox.setChecked(dejaPreferences.getBoolean(IsViewingFriends, true));
        sharePhotosBox.setChecked(dejaPreferences.getBoolean(IsSharingPhotos, true));

        if(dejaPreferences.getBoolean(IsDejaVuModeOn, true)) {
            dejavuSwitch.setOnCheckedChangeListener(null);
            dejavuSwitch.setChecked(true);
            dejavuSwitch.setOnCheckedChangeListener(dejavuSwitchListener);

                /* Check if user has locationSwitch enabled */
            if(dejaPreferences.getBoolean(IsLocationOn, true)) {
                locationSwitch.setOnCheckedChangeListener(null);
                locationSwitch.setChecked(true);
                locationSwitch.setOnCheckedChangeListener(locationSwitchListener);
            }

            else {
                locationSwitch.setChecked(true);
                locationSwitch.setChecked(false);
            }

                /* Check if user has locationSwitch on */
            if(dejaPreferences.getBoolean(IsTimeOn, true)) {
                timeSwitch.setOnCheckedChangeListener(null);
                timeSwitch.setChecked(true);
                timeSwitch.setOnCheckedChangeListener(timeSwitchListener);
            }

            else {
                timeSwitch.setChecked(true);
                timeSwitch.setChecked(false);
            }

                /* Check if user has dateSwitch on */
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

        /* Check what update interval the user has set */
        updateIntervalBar.setProgress(dejaPreferences.getInt(UpdateInterval, 300000)/60000 - 1);
    }

    //Back button click handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    /* Method used to easily return the new text that the UI should be
     * displaying when settings change */
    private String newText(String settingText, boolean isChecked){
        if (isChecked){
            return settingText + " enabled";
        }
        else {
            return settingText + " disabled";
        }
    }

    public void shareStarter( boolean isChecked )
    {
        Log.d(TAG, "shareStarter called");
        Intent shareIntent = new Intent(SettingsActivity.this,SharingService.class);
        shareIntent.putExtra( "loadOrRemove", isChecked );
        startService(shareIntent);
    }
    public void shareStopper()
    {
        Log.d(TAG, "shareStopper called");
        Intent shareIntent = new Intent(SettingsActivity.this,SharingService.class);
        stopService(shareIntent);
    }
}
