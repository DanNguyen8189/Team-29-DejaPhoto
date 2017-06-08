package com.team29.cse110.team29dejaphoto.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.team29.cse110.team29dejaphoto.R;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.models.RemotePhoto;
import com.team29.cse110.team29dejaphoto.services.PhotoService;

public class CustomLocationActivity extends AppCompatActivity {

    private DejaPhoto currDisplayedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_location);

        //TODO CURRDISPLAYEDPHOTO IS NOT THE CORRECT OBJECT WE NEED TO BE LOOKING AT!!!!!!!!!!!!!
        if(currDisplayedPhoto instanceof RemotePhoto) {
            Toast.makeText(CustomLocationActivity.this, "Cannot edit the location of a friend's photo",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void cancel(View view) {
        finish();
    }

    //TODO - get the location from the editText and update the bitmap/photo
    public void done(View view) {
        finish();
    }

}
