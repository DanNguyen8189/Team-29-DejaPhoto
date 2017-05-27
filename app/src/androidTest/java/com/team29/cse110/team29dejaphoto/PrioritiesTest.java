package com.team29.cse110.team29dejaphoto;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

/**
 * Created by tyler on 5/9/17.
 */
public class PrioritiesTest {

    private Priorities p;
    DejaPhoto[] gallery;
    DejaPhoto newPhoto;
    Calendar calendar = Calendar.getInstance();
    Long time;
    Preferences prefAll = new Preferences(true, true, true);
    Preferences prefNoTime = new Preferences(true, true, false);
    Preferences prefNoLoc = new Preferences(false, true, true);
    Preferences prefNone = new Preferences(false, false, false);

    private String TAG = "PrioritiesTest";

    /**
     * A helper method for instantiating test objects before every test.
     * ** NOTE ** to test this method between the hours of 12am-3am, add 3 hours to calendar instead
     *            of subtracting 3 hours.
     */
    @Before
    public void setUp() {
        p = new Priorities();
        time = calendar.getTimeInMillis();// The current time, maximum deja vu.
        calendar.add(Calendar.HOUR, -3);// Subtract 3 hours from the time

        // Priority is element 2, 1, 0 when all options are on.
        gallery = new DejaPhoto[]{
                new DejaPhoto(null, 0, 0, calendar.getTimeInMillis()), // #1 20 points
                new DejaPhoto(null, 300, 300, calendar.getTimeInMillis()), //#3 10 points
                new DejaPhoto(null, 300, 300, time), // #2 10 points
                new DejaPhoto(null, 0, 0, time)}; // #0 20
        newPhoto = new DejaPhoto(null, 0, 0, 0L);

    }


    /**
     * Tests that inserting a photo into the priorities is successful.
     *
     * @throws Exception
     */
    @Test
    public void add() throws Exception {
        // Test adding non-null photo; photos added in priorities are always instantiated
        assertTrue(p.add(newPhoto));
        Log.d(TAG, "Testing add() method");
    }


    /**
     * Tests that photos are returned from priority queue in the correct order
     *
     * @throws Exception
     */
    @Test
    public void getNewPhoto() throws Exception {

        Location location = new Location("");
        location.setLongitude(0);
        location.setLatitude(0);

        for (DejaPhoto d : gallery) {
            p.add(d);
        }
        p.updatePriorities(location, prefAll);



        assertTrue(p.getNewPhoto().equals(gallery[3]));
        assertTrue(p.getNewPhoto().equals(gallery[0]));
        assertTrue(p.getNewPhoto().equals(gallery[2]));
        assertTrue(p.getNewPhoto().equals(gallery[1]));


        // When time is off, element 2 and 1 have same priority, 0 is lowest
        for (DejaPhoto d : gallery) {
            p.add(d);
            p.updatePriorities(location, prefNoTime);
        }
        assertTrue(p.getNewPhoto().equals(gallery[3]));
        assertTrue(p.getNewPhoto().equals(gallery[0]));
        assertTrue(p.getNewPhoto().equals(gallery[2]));
        assertTrue(p.getNewPhoto().equals(gallery[1]));


        // When all are off, the return order is reverse input order
        for (DejaPhoto d : gallery) {
            p.add(d);
        }
        p.updatePriorities(location, prefNone);

        assertTrue(p.getNewPhoto().equals(gallery[0]));
        assertTrue(p.getNewPhoto().equals(gallery[1]));
        assertTrue(p.getNewPhoto().equals(gallery[2]));
        assertTrue(p.getNewPhoto().equals(gallery[3]));

        Log.d(TAG,"Test getNewPhoto() method");
    }


    /**
     * Tests that priority queue is properly updated when the updatePriorities method is called.
     *
     * @throws Exception
     */
    @Test
    public void updatePriorities() throws Exception {
        p = new Priorities();
        gallery[0] = new DejaPhoto(null, 0, 0, time);
        gallery[1] = new DejaPhoto(null, 0, 0, calendar.getTimeInMillis());// subtract 3 hr
        p.updatePriorities(new Location(""), prefAll);
        p.add(gallery[0]);
        p.add(gallery[1]);

        assertTrue(p.getNewPhoto().equals(gallery[0]));
        assertTrue(p.getNewPhoto().equals(gallery[1]));

        Log.d(TAG,"Testing updatePriorities");
    }
}