package com.team29.cse110.team29dejaphoto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton arrowLeft; //left arrow button
    ImageButton arrowRight; //right arrow button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrowLeft = (ImageButton) findViewById(R.id.leftArrow);
        arrowRight = (ImageButton) findViewById(R.id.rightArrow);

        /*
        TODO
        arrowLeft.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        }*/
    }
}
