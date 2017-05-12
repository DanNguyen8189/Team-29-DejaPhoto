package com.team29.cse110.team29dejaphoto;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    boolean useDefaultGallery = true;

    TextView appToggle;
    TextView dejavuToggle;
    TextView locationToggle;
    TextView timeToggle;
    TextView dateToggle;
    Switch appOnOff;
    Switch dejavu;
    Switch location;
    Switch time;
    Switch date;

    RadioGroup radio;

    private final int PERMISSIONS_REQUEST_MEDIA = 1; // int value for permission to access MEDIA
    private final int PERMISSIONS_LOCATION = 2; // int value for permission to access location

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radio = (RadioGroup) findViewById(R.id.RadioGroup);

        appToggle = (TextView) findViewById(R.id.appSwitch);
        dejavuToggle = (TextView) findViewById(R.id.dejavuText);
        locationToggle = (TextView) findViewById(R.id.locationText);
        timeToggle = (TextView) findViewById(R.id.timeText);
        dateToggle = (TextView) findViewById(R.id.dateText);

        appOnOff = (Switch) findViewById(R.id.serviceButton);
        dejavu = (Switch) findViewById(R.id.dejavuMode);
        location = (Switch) findViewById(R.id.location);
        time = (Switch) findViewById(R.id.time);
        date = (Switch) findViewById(R.id.date);

        appOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appToggle.setText("DejaPhoto is enabled");
                    starter();
                }

                else {
                    appToggle.setText("DejaPhoto is disabled");
                    stopper();
                }
            }
        });

        dejavu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dejavuToggle.setText("DejaVu enabled");
                    location.setChecked(true);
                    time.setChecked(true);
                    date.setChecked(true);
                    location.setClickable(true);
                    time.setClickable(true);
                    date.setClickable(true);
                }

                else {
                    dejavuToggle.setText("DejaVu disabled");
                    location.setChecked(false);
                    time.setChecked(false);
                    date.setChecked(false);
                    location.setClickable(false);
                    time.setClickable(false);
                    date.setClickable(false);
                }
            }
        });

        location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    locationToggle.setText("Location enabled");
                }

                else {
                    locationToggle.setText("Location disabled");
                }
            }
        });

        time.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timeToggle.setText("Time enabled");
                }

                else {
                    timeToggle.setText("Time disabled");
                }
            }
        });

        date.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dateToggle.setText("Date enabled");
                }

                else {
                    dateToggle.setText("Date disabled");
                }
            }
        });

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_LOCATION);
            return;
        }

    }

    public void onClickLoadPhotos() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_MEDIA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch ( requestCode ) {
            case PERMISSIONS_REQUEST_MEDIA : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Done Loading Photos", Toast.LENGTH_SHORT).show();
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
            default: {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    public void starter() {
        Log.d(TAG, "Starter button pushed");
        Intent intent = new Intent(MainActivity.this, PhotoService.class);

        onClickLoadPhotos();
        startService(intent);
    }

    public void stopper() {
        Log.d(TAG, "Stopper button pushed");
        Intent intent = new Intent(MainActivity.this, PhotoService.class);
        stopService(intent);
    }



    public void onClickRadioButton(View view) {

        switch(view.getId()) {

            case R.id.DefaultAlbum:
                if(useDefaultGallery) {
                    break;
                }
                else {
                    useDefaultGallery = !useDefaultGallery;
                    break;
                }
            case R.id.DejaAlbum:
                if(useDefaultGallery) {
                    useDefaultGallery = !useDefaultGallery;
                    break;
                }
                else {
                    break;
                }
        }

    }

    public void onCreateCustomAlbum(View view) {

        Intent intent = new Intent(getApplicationContext(), CustomAlbumActivity.class);
        startActivity(intent);
    }

}
