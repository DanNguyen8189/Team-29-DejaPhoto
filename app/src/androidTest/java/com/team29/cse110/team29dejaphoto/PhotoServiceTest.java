package com.team29.cse110.team29dejaphoto;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by RobertChance on 5/14/17.
 */
public class PhotoServiceTest {

    DejaPhoto[] gallery = new DejaPhoto[15];
    DejaPhoto[] smallGallery = new DejaPhoto[3];
    Location location = new Location("");
    DisplayCycle displayCycle = new DisplayCycle();
    DisplayCycle smallDisplayCycle = new DisplayCycle();

    private String TAG = "PhotoServiceTest";


    @Before
    public void setUp() {

        // Populate gallery with dummy images of all the same score
        for(int i = 0; i < 15; i++) {
            gallery[i] = new DejaPhoto(Uri.EMPTY, 0, 0, Calendar.getInstance().getTimeInMillis());
            displayCycle.addToCycle(gallery[i]);
        }
        for(int i = 0; i < 3; i++ ) {

            smallGallery[i] = new DejaPhoto(Uri.EMPTY, 0, 0, Calendar.getInstance().getTimeInMillis());
            smallDisplayCycle.addToCycle(gallery[i]);
        }


    }

    @Test
    public void cycleForward() throws Exception {

        // Test that displayCycle has continuous access to a next photo
        for(int i = 0; i < 500; i++ ) {

            assertNotNull(displayCycle.getNextPhoto());

        }

        Log.d(TAG, "Testing cycleForward() method");
    }



    @Test
    public void cycleBack() throws Exception {

        assertNull(displayCycle.getPrevPhoto());

        Log.d(TAG, "Testing cycleBack() method");

    }


    @Test
    public void releasePhoto() throws Exception {


        //CHECK FULL DISPLAY CYCLE
        // Check that photo is released when at beginning of cycle
        DejaPhoto d = displayCycle.getNextPhoto();
        displayCycle.removeCurrPhotoFromHistory();
        for(int i = 0; i < 15; i++) {
            DejaPhoto d2 = displayCycle.getNextPhoto();
            assertFalse(d.equals(d2));
        }

         // Check that a previous can be removed, when in start of history.
        d = displayCycle.getPrevPhoto();
        displayCycle.removeCurrPhotoFromHistory();
        for(int i = 0; i < 15; i++) {
            DejaPhoto d2 = displayCycle.getNextPhoto();
            System.out.println("TESTTTTTTTTTT " + i);
            assertFalse(d.equals(d2));
        }

        // CHECK SMALL DISPLAY CYCLE
        // Check that photo is released when at beginning of cycle
        d = smallDisplayCycle.getNextPhoto();
        smallDisplayCycle.removeCurrPhotoFromHistory();
        for(int i = 0; i < 4; i++ ) {
            DejaPhoto d2 = smallDisplayCycle.getNextPhoto();
            assertFalse(d.equals(d2));
        }

        // check that photo is realease when in history
        d = smallDisplayCycle.getPrevPhoto();
        smallDisplayCycle.removeCurrPhotoFromHistory();
        for(int i = 0; i < 4; i++ ) {
            DejaPhoto d2 = smallDisplayCycle.getNextPhoto();
            assertFalse(d.equals(d2));
        }
    }

    @Test
    public void givePhotoKarma() throws Exception {

    }

}