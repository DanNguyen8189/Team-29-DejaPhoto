package com.team29.cse110.team29dejaphoto;


import android.location.Location;
import android.util.Log;

import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.models.Preferences;
import com.team29.cse110.team29dejaphoto.models.RemotePhoto;
import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tyler on 6/5/17.
 */
public class RemotePhotoTest {

    DejaPhoto emptyPhoto;// no score
    DejaPhoto dejaPhoto;// score with current time and this location
    Calendar calendar;
    Location location;

    private final String TAG = "RemotePhotoTest";

    @Before
    public void setUp() {

        calendar = Calendar.getInstance();
        location = new Location("");
        location.setLongitude(-117.2340);
        location.setLatitude(32.8801);// UCSD location
        // create a new remote photo with no bitmap, location or time information.
        emptyPhoto = new RemotePhoto(null,0,0,0,0,false, null);
        dejaPhoto = new RemotePhoto(null,0,location.getLatitude(),location.getLongitude(),
                                    calendar.getTimeInMillis(),false, null);
    }

    /**
     * Tests that remote photos are properly compared for the priority queue.
     *
     * @throws Exception
     */
    @Test
    public void compareTo() throws Exception {

        ((RemotePhoto)dejaPhoto).setScore(20);
        assertEquals("Both photos have same score", 0, dejaPhoto.compareTo(dejaPhoto));
        assertEquals("This photo has higher score", 1, dejaPhoto.compareTo(emptyPhoto));
        assertEquals("Other photo has higher score", -1, emptyPhoto.compareTo(dejaPhoto));

        Log.d(TAG, "Testing compareTo() method");
    }



    /**
     * Tests that karma is added to a photo, and that once added cannot add again
     *
     * @throws Exception
     */
    @Test
    public void addKarma() throws Exception {

        // dejaPhoto has no karma
        assertTrue(dejaPhoto.getKarma()==0);

        // add karma
        dejaPhoto.addKarma();
        assertTrue(dejaPhoto.getKarma()==1);

        // add karma again, but cannot add more than once
        dejaPhoto.addKarma();
        assertTrue(dejaPhoto.getKarma()==1);

        Log.d(TAG, "Testing addKarma() method");
    }


    /**
     * Tests that the score for a time match is correct
     *
     * @throws Exception
     */
    @Test
    public void getTimeTakenPoints() throws Exception {

        assertTrue(((RemotePhoto)emptyPhoto).getTimeTakenPoints() == 0);
        assertTrue(((RemotePhoto)dejaPhoto).getTimeTakenPoints() == 10);

        Log.d(TAG, "Testing getTimeTakenPoints() method");
    }


    /**
     * Tests that the score for a date match is correct
     *
     * @throws Exception
     */
    @Test
    public void getDatePoints() throws  Exception {

        assertTrue(((RemotePhoto)emptyPhoto).getDatePoints() == 0);
        assertTrue(((RemotePhoto)dejaPhoto).getDatePoints() == 10);

        Log.d(TAG, "Testing getDatePoints() method");
    }


    /**
     * Tests that the score for a location match is correct
     *
     * @throws Exception
     */
    @Test
    public void getLocationPoints() throws Exception {

        assertTrue(((RemotePhoto)emptyPhoto).getDatePoints() == 0);
        assertTrue(((RemotePhoto)dejaPhoto).getDatePoints() == 10);

        Log.d(TAG, "Testing getLocationPoints() method");
    }



    /**
     * Tests that latitude and longitude of location are properly returned
     *
     * @throws Exception
     */
    @Test
    public void getLocation() throws Exception {

        assertTrue(dejaPhoto.getLocation().getLatitude() == location.getLatitude());
        assertTrue(dejaPhoto.getLocation().getLongitude() == location.getLongitude());

        Log.d(TAG, "Testing getLocation() method");
    }


    /**
     * This tests that a photo's score is properly updated with dejavu flags on and off
     *
     * @throws Exception
     */
    @Test
    public void updateScore() throws Exception {

        assertEquals("photo has score of 0", 0, dejaPhoto.getScore());

        // dejaPhoto has time, date and location dejaVu
        dejaPhoto.updateScore(location,new Preferences(true,true,true));
        assertTrue(dejaPhoto.getScore() == 30 );

        // dejaPhoto has time and date, location disabled
        dejaPhoto.updateScore(location,new Preferences(false,true,true));
        assertTrue(dejaPhoto.getScore() == 20);

        // dejaPhoto has only time,  date and location disabled
        dejaPhoto.updateScore(location,new Preferences(false,false,true));
        assertTrue(dejaPhoto.getScore() == 10);

        // dejaPhoto has no score with all fields disabled
        dejaPhoto.updateScore(location,new Preferences(false,false,false));
        assertTrue(dejaPhoto.getScore() == 0);

        Log.d(TAG, "Testing updateScore() method");
    }
}