package com.team29.cse110.team29dejaphoto;


import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tyler on 5/9/17.
 */
public class DejaPhotoTest {

    Calendar calendar;
    DejaPhoto photo;// A photo object to be modified
    DejaPhoto emptyPhoto;// An empty photo
    DejaPhoto dejaVuTime;// A photo with deja vu in time only
    DejaPhoto dejaVuDate;// A photo with deja vu in date only
    DejaPhoto dejaVuAll; // A photo with deja vu in time and date


    /**
     * A helper function to instantiate the test objects before each test
     */
    @Before
    public void setUp() {

        calendar = Calendar.getInstance();

        // This photo will be left for modification
        photo = new DejaPhoto(Uri.EMPTY, 0, 0, 0L);

        // Empty photo with null parameters
        emptyPhoto = new DejaPhoto(null, 0, 0, 0L);

        // Photo from this instant in time, deja vu for time and date
        dejaVuAll = new DejaPhoto(Uri.EMPTY, 0, 0, Calendar.getInstance().getTimeInMillis());

        // Adjust calendar to same day last week, but 3 hours earlier.  Only deja vu in date
        calendar.add(Calendar.DAY_OF_WEEK, -7);// 1 week ago
        calendar.add(Calendar.HOUR, -3);// 3 hours earlier
        dejaVuDate = new DejaPhoto(Uri.EMPTY, 0, 0, calendar.getTimeInMillis());

        // Adjust calendar to different day of week, but within current 2 hrs. Only deja vu in time
        calendar.add(Calendar.DAY_OF_WEEK,5);
        calendar.add(Calendar.HOUR, 2);
        dejaVuTime = new DejaPhoto(Uri.EMPTY, 0, 0, calendar.getTimeInMillis());
    }


    /**
     * This tests the the correct scores of the photos are returned.
     *
     * @throws Exception
     */
    @Test
    public void getScore() throws Exception {
        assertEquals("Score of dejaVuAll:", 20, dejaVuAll.getScore());
        assertEquals("Score of dejaVuTime:", 10, dejaVuTime.getScore());
        assertEquals("Score of dejaVuDate:", 10, dejaVuDate.getScore());
    }

    /**
     * This tests that setting a score will properly modify the instance variable.
     *
     * @throws Exception
     */
    @Test
    public void setScore() throws Exception {

        assertFalse(photo.getScore()==20);
        photo.setScore(20);
        assertEquals("Testing that photo score is set", 20, photo.getScore());
    }


    /**
     * This tests that photos with null and non-null URI can return the URI.  Expected behavior
     * for null URI is return null.  No URI is expected to be null, so no handling is done.
     *
     * @throws Exception
     */
    @Test
    public void getPhotoUri() throws Exception {
        assertNull(emptyPhoto.getPhotoUri());
        assertNotNull(photo.getPhotoUri());
    }

    /**
    @Test
    public void getKarma() throws Exception {

    }

    @Test
    public void setKarma() throws Exception {

    }
     */



    /**
     * Tests that shown recently flag is correct for newly created photos.
     *
     * @throws Exception
     */
    @Test
    public void isShownRecently() throws Exception {
        assertFalse(photo.isShownRecently());
    }



    /**
     * Tests that instance variable is properly updated.
     *
     * @throws Exception
     */
    @Test
    public void setShowRecently() throws Exception {
        photo.setShowRecently();
        assertTrue(photo.isShownRecently());
    }


    /**
     * Tests that the time of zero-initialized photos is 0, also that photos with a valid time are
     * non-zero.
     *
     * @throws Exception
     */
    @Test
    public void getTime() throws Exception {
        assertEquals("Testing empty photo has 0 time", 0, emptyPhoto.getTime().getTimeInMillis());
        assertEquals("Testing dejaVu photo has some time", false,
                     dejaVuAll.getTime().getTimeInMillis() == 0);
    }


    /**
     * Tests that the time instance variable is properly updated.
     *
     * @throws Exception
     */
    @Test
    public void setTime() throws Exception {

        assertEquals("Testing photo has 0 time", 0, photo.getTime().getTimeInMillis());
        photo.setTime(Calendar.getInstance());
        assertEquals("Testing dejaVu photo has some time", false,
                      photo.getTime().getTimeInMillis() == 0);
    }


    /**
     * Tests that scores of photos are properly compared.
     *
     * @throws Exception
     */
    @Test
    public void compareTo() throws Exception {
        assertEquals("Test comparing two empty photo objects", photo.compareTo(emptyPhoto), 0);
        photo.setScore(20);
        assertEquals("Test comparing photo with emptyPhoto", photo.compareTo(emptyPhoto), 1);
        assertEquals("Test comparing emptyPhoto with photo", emptyPhoto.compareTo(photo), -1);
    }


    /**
     * Tests that scores are properly updated when the updateScore method is run.
     *
     * @throws Exception
     */
    @Test
    public void updateScore() throws Exception {

        assertTrue(dejaVuAll.compareTo(dejaVuDate) == 1);
        assertTrue(dejaVuAll.compareTo(dejaVuTime) == 1);
        assertTrue(dejaVuDate.compareTo(dejaVuTime) == 0);

        // set dejaVuTime to current time, giving it highest priority
        dejaVuTime.setTime(Calendar.getInstance());
        assertFalse(dejaVuTime.getScore()>=dejaVuAll.getScore());// Score hasn't changed yet
        dejaVuTime.updateScore(true, true, true);// Update score by increasing
        assertTrue(dejaVuTime.getScore()>=dejaVuAll.getScore());

        // Update calendar to a time with no deja vu.
        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -3);
        calendar.add(Calendar.DAY_OF_WEEK, -2);
        dejaVuTime.setTime(calendar);

        assertTrue(dejaVuTime.getScore()>=dejaVuAll.getScore());
        dejaVuTime.updateScore(true,true,true);// Update score by decreasing
        assertFalse(dejaVuTime.getScore()>=dejaVuAll.getScore());

    }

}