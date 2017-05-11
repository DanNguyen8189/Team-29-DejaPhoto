package com.team29.cse110.team29dejaphoto;

import android.net.Uri;

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

    /**
     * A helper method for instantiating test objects before every test.
     */
    @Before
    public void setUp() {
        p = new Priorities();
        time = calendar.getTimeInMillis();// The current time, maximum deja vu.
        calendar.add(Calendar.HOUR, -3);// Subtract 3 hours from the time

        // Element 2 should have priority over elements 0 and 1
        gallery = new DejaPhoto[] { new DejaPhoto(null, 0, 0, calendar.getTimeInMillis()),
                                    new DejaPhoto(null, 0, 0, calendar.getTimeInMillis()),
                                    new DejaPhoto(null, 0, 0, time)};
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
    }



    /**
     * Tests that photos are returned from priority queue in the correct order
     *
     * @throws Exception
     */
    @Test
    public void getNewPhoto() throws Exception {
        for (DejaPhoto d:gallery) {
            p.add(d);
        }
        assertTrue(p.getNewPhoto().equals(gallery[2]));
        assertTrue(p.getNewPhoto().equals(gallery[0]));
        assertTrue(p.getNewPhoto().equals(gallery[1]));
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
        p.updatePriorities();
        p.add(gallery[0]);
        p.add(gallery[1]);

        assertTrue(p.getNewPhoto().equals(gallery[0]));
        assertTrue(p.getNewPhoto().equals(gallery[1]));

    }

    @Test
    public void calcScoreof() throws Exception {
        p = new Priorities();
        calendar = Calendar.getInstance();

        assertEquals("Get score of photo 0", 0, p.calcScoreOf(gallery[0]) );
        assertEquals("Get score of photo 1", 0, p.calcScoreOf(gallery[1]) );
        assertEquals("Get score of photo 2", 20, p.calcScoreOf(gallery[2]) );

        // Give photo 0 time and day deja vu
        gallery[0].setTime(calendar);
        assertEquals("Photo 0 now has both time and day deja vu", 20, p.calcScoreOf(gallery[0]));

        calendar.add(Calendar.HOUR, 3);// add 3 hours to calendar

        // Give photo 1 only day deja vu
        gallery[1].setTime(calendar);
        assertEquals("Photo 1 now has day deja vu", 10, p.calcScoreOf(gallery[1]));

        // Give photo 2 only time deja vu
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK,-2);

        gallery[2].setTime(calendar);
        assertEquals("Photo 2 now has only time deja vu", 10, p.calcScoreOf(gallery[2]));

        // Give photo 2 no deja vu
        calendar.add(Calendar.HOUR, 3);
        gallery[2].setTime(calendar);
        assertEquals("Photo 2 now has no deja vu", 0, p.calcScoreOf(gallery[2]));

    }
}