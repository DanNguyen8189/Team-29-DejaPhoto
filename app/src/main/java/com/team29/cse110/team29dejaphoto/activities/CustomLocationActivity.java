package com.team29.cse110.team29dejaphoto.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.team29.cse110.team29dejaphoto.R;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.models.RemotePhoto;

public class CustomLocationActivity extends AppCompatActivity {

    public static SharedPreferences dejaPreferences; // Holds the reference to the SharedPreferences file
    public static final String DEJA_PREFS = "Deja_Preferences"; // SharedPreference file key

    //intialize buttons and text areas
    TextView enterCustomLocationText;
    EditText editText;
    Button setLocationButton;
    Button cancelLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_location);

        enterCustomLocationText = (TextView) findViewById(R.id.enter_custom_location_text);
        editText = (EditText) findViewById(R.id.edit_text);
        setLocationButton = (Button) findViewById(R.id.set_location_button);
        cancelLocationButton = (Button) findViewById(R.id.cancel_location_button);

        // sharedpref will hold the custom location the user wants for a particular photo
        dejaPreferences = getSharedPreferences(DEJA_PREFS, 0);
        SharedPreferences.Editor editor  = dejaPreferences.edit();
        editor.putString("customLocation", "");

    }

    public void cancel(View view) {
        dejaPreferences = getSharedPreferences(DEJA_PREFS, 0);
        SharedPreferences.Editor editor  = dejaPreferences.edit();
        editor.putBoolean("wantedCustomLocation", false);
        editor.commit();
        finish();
    }

    //TODO - get the location from the editText and update the bitmap/photo
    public void done(View view) {
        dejaPreferences = getSharedPreferences(DEJA_PREFS, 0);
        SharedPreferences.Editor editor  = dejaPreferences.edit();
        editor.putBoolean("wantedCustomLocation", true);
        editor.commit();
        finish();
    }

}
