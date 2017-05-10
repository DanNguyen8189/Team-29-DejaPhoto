package com.team29.cse110.team29dejaphoto;

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
    Long pastTime;

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

}