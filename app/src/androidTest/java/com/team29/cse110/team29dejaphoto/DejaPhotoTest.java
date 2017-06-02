package com.team29.cse110.team29dejaphoto;


import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.team29.cse110.team29dejaphoto.models.DejaPhoto;
import com.team29.cse110.team29dejaphoto.models.Preferences;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tyler on 5/9/17.
 */
public class DejaPhotoTest {

    private String TAG = "DejaPhoto test";

    Calendar calendar;
    DejaPhoto photo;// A photo object to be modified
    DejaPhoto emptyPhoto;// An empty photo
    DejaPhoto dejaVuTime;// A photo with deja vu in time only
    DejaPhoto dejaVuDate;// A photo with deja vu in date only
    DejaPhoto dejaVuAll; // A photo with deja vu in time and dateS
    DejaPhoto dejaVuLocation;// a Photo with deja vu in only location
    DejaPhoto noDejaVu;// A photo with no deja vu

    Preferences prefAllOn = new Preferences(true,true,true);


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
        calendar = Calendar.getInstance();
       // calendar.set(2017, 05, 13, 12, 47);
        calendar.add(Calendar.DAY_OF_WEEK, -7);// 1 week ago
        calendar.add(Calendar.HOUR, 3);// 3 hours later
        dejaVuDate = new DejaPhoto(Uri.EMPTY, 0, 0, calendar.getTimeInMillis());

        // Adjust calendar to different day of week, but within current 2 hrs. Only deja vu in time
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK,-2);
        dejaVuTime = new DejaPhoto(Uri.EMPTY, 0, 0, calendar.getTimeInMillis());

        // Adjust calender to different time, and add local location
        calendar.add(Calendar.HOUR,3 );// calendar has no time or date deja vu
        dejaVuLocation = new DejaPhoto(Uri.EMPTY, 0, 0, calendar.getTimeInMillis());

        // Adjust calendar to different day, add non-local location
        calendar.add(Calendar.DAY_OF_WEEK, 3);
        noDejaVu = new DejaPhoto(Uri.EMPTY, 300, 300, calendar.getTimeInMillis());
    }


    /**
     * This tests the the correct scores of the photos are returned.
     *
     * @throws Exception
     */
    @Test
    public void updateScore() throws Exception {

        Location location = new Location("");
        assertEquals("Score of dejaVuAll:", 30, dejaVuAll.updateScore(location, prefAllOn));
        assertEquals("Score of dejaVuTime:", 20, dejaVuTime.updateScore(location, prefAllOn));
        assertEquals("Score of dejaVuDate:", 20, dejaVuDate.updateScore(location, prefAllOn));
        assertEquals("Score of dejaVuLocation:", 10, dejaVuLocation.updateScore(location,prefAllOn));
        assertEquals("Score of noDejaVu", 0, noDejaVu.updateScore(location,prefAllOn));
        Log.d(TAG, "Testing updateScore() method");
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
        Log.d(TAG, "Testing getPhotoUri() method");
    }


    /** This tests that the karma of a photo is properly returned
     *
     * @throws Exception
     */
    @Test
    public void getKarma() throws Exception {

        assertFalse(photo.getKarma());
        Log.d(TAG, "Testing getKarma() method");
    }


    /**
     * This tests that the karma of a photo is properly set and can be returned
     *
     * @throws Exception
     */
    @Test
    public void setKarma() throws Exception {

        photo.setKarma();
        assertTrue(photo.getKarma());
        Log.d(TAG,"Testing setKarma() method");
    }


    /**
     * Tests that shown recently flag is correct for newly created photos.
     * Default for newly created photos is always false
     *
     * @throws Exception
     */
    @Test
    public void isShownRecently() throws Exception {
        assertFalse(photo.isShownRecently());
        Log.d(TAG,"Testing isShownRecently() method");
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
        Log.d(TAG,"Testing setShowRecently() method");
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
        Log.d(TAG,"Testing getTime() method");
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
        Log.d(TAG,"Testing setTime() method");
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
        Log.d(TAG,"Testing compareTo() method");
    }


    /**
     * This tests that the location score of a photo object is properly updated.
     * At 38 degrees latitude, 1 second ~ 80 ft, therefore 1000ft ~ .00347
     *
     * @throws Exception
     */
    @Test
    public void testLocation() throws Exception {

        Location inBounds = new Location("");
        inBounds.setLongitude(0.0034);
        inBounds.setLatitude(38);

        Location outBounds = new Location("");
        outBounds.setLongitude(0.005);
        outBounds.setLongitude(38);

        Location reference = new Location("");
        reference.setLongitude(0);
        reference.setLatitude(38);

        dejaVuLocation.setLocation(inBounds);
        noDejaVu.setLocation(outBounds);

        assertTrue(dejaVuLocation.updateScore(reference, prefAllOn)
                     > noDejaVu.updateScore(reference, prefAllOn));
    }
}

