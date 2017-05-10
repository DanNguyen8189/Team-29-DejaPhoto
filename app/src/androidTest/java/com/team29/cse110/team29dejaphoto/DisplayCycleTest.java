package com.team29.cse110.team29dejaphoto;

import java.util.Calendar;
import android.net.Uri;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by tyler on 5/8/17.
 *
 * These tests test the functionality of the Display cycle, including retrieving next and
 * previous photos, adding photos to the cycle, loading photos to the cycle.
 */
public class DisplayCycleTest {

    // Create a new DisplayCycle
    private DisplayCycle ds = new DisplayCycle();

    Calendar calendar = Calendar.getInstance();

    // Create a few dummy Dejaphotos
    private DejaPhoto one;
    private DejaPhoto two;
    private DejaPhoto three;

    // Create new DejaPhoto Galleries and populate them
    private DejaPhoto[] testGalleryEmpty = {};
    private DejaPhoto[] testGalleryOneElement = new DejaPhoto[1];
    private DejaPhoto[] testGalleryThreeElements = new DejaPhoto[3];
    private DejaPhoto[] testGalleryManyElements;// larger than history size

    /**
     * A helper function to instantiate the test objects before each case.
     */
    @Before
    public void setUp() {
        one = new DejaPhoto(null, 0, 0, calendar.getTimeInMillis());
        calendar.add(Calendar.HOUR,1);
        two = new DejaPhoto(null, 0, 0, calendar.getTimeInMillis());
        calendar.add(Calendar.HOUR,3);
        three = new DejaPhoto(null, 0, 0, calendar.getTimeInMillis());

        // Create new DejaPhoto Galleries and populate them
        testGalleryOneElement[0] = one;
        testGalleryThreeElements[0] = one;
        testGalleryThreeElements[1] = two;
        testGalleryThreeElements[2] = three;
        testGalleryManyElements = fillTestGallery();
    }


    /**
     * This tests that both an empty and non-empty photo gallery can be loaded.  Expected behavior
     * for loading an empty gallery is a successful load, and display cycle is "empty".
     *
     * @throws Exception
     */
    @Test
    public void testFillDisplayCycle() throws Exception {

        // Test that empty display cycle is successfully filled
        assertTrue(ds.fillDisplayCycle(testGalleryEmpty));
        // Test that non-empty display cycle is successfully filled
        assertTrue(ds.fillDisplayCycle(testGalleryOneElement));
    }


    /**
     * This tests that a photo can be added to both an empty and a non-empty display cycle
     *
     * @throws Exception
     */
    @Test
    public void addToCycle() throws Exception {

        // Test that photo is successfully added to an empty cycle
        ds.fillDisplayCycle(testGalleryEmpty);
        assertTrue(ds.addToCycle(new DejaPhoto(null, 0, 0, 0L)));

        // Test that photo is successfully added to a non-empty cycle
        ds.fillDisplayCycle(testGalleryOneElement);
        assertTrue(ds.addToCycle(new DejaPhoto(null, 0, 0, 0L)));
    }


    /**
     * This tests retrieving the next photo for various states of the display cycle. It tests
     * conditions including: next on empty cycle, next with only one element, next with many
     * elements (a full history).  Expected behavior of next on empty is return null, which is
     * handled by the caller.  Expected behavior of next on non-empty list is as follows:
     * if empty priority queue and not in history, cycle last photo in history to display
     * if empty priority queue and in history, get previous history photo
     * if non empty priority queue, get highest priority photo
     *
     * @throws Exception
     */
    @Test
    public void getNextPhoto() throws Exception {

        // Test get next on an empty cycle
        ds.fillDisplayCycle(testGalleryEmpty);
        assertNull(ds.getNextPhoto());

        // Test get next on a one-element set
        ds = new DisplayCycle();
        ds.fillDisplayCycle(testGalleryOneElement);
        assertTrue(ds.getNextPhoto().equals(one));
        assertTrue(ds.getNextPhoto().equals(one)); // NOTE: Perhaps not ideal functionality

        // Test get next on a three-element set
        ds = new DisplayCycle();
        ds.fillDisplayCycle(testGalleryThreeElements);
        assertTrue(ds.getNextPhoto().equals(one));
        assertTrue(ds.getNextPhoto().equals(two));
        assertTrue(ds.getNextPhoto().equals(three));
        assertTrue(ds.getNextPhoto().equals(one));

        // Test get next on a full history set
        ds = new DisplayCycle();
        ds.fillDisplayCycle(testGalleryManyElements);
        ds.getNextPhoto();
        for(int i = testGalleryManyElements.length - 1; i >= 0 ; i-- ) {
            assertTrue(ds.getNextPhoto().equals(testGalleryManyElements[i]) );
        }
    }


    /**
     * This tests retrieving a previous photo for various states of the display cycle.  It tests
     * getting previous on an empty list.  Expected behavior is return null, handled by caller.
     * It also tests getting previous on a single element set and a full history set.  Expected
     * behavior for getting prev at end of history is returning null.
     *
     * @throws Exception
     */
    @Test
    public void getPrevPhoto() throws Exception {

        // Test get prev on an empty cycle
        ds.fillDisplayCycle(testGalleryEmpty);
        assertNull(ds.getPrevPhoto());

        // Test get prev on a one-element set
        ds = new DisplayCycle();
        ds.fillDisplayCycle(testGalleryOneElement);
        assertNull(ds.getPrevPhoto());
        ds.getNextPhoto();
        assertNull(ds.getPrevPhoto());

        // Test get prev on a three-element set
        ds = new DisplayCycle();
        ds.fillDisplayCycle(testGalleryThreeElements);
        ds.getNextPhoto();
        ds.getNextPhoto();
        ds.getNextPhoto();
        ds.getNextPhoto();// This is top of list with prev history
        assertTrue(ds.getPrevPhoto().equals(three));
        assertTrue(ds.getPrevPhoto().equals(two));
        assertNull(ds.getPrevPhoto());// end of history

        // Test get prev on a full history set
        ds = new DisplayCycle();
        ds.fillDisplayCycle(testGalleryManyElements);
        ds.getNextPhoto();
        for(int i = testGalleryManyElements.length - 1; i >= 0 ; i-- ) {
            ds.getNextPhoto();
        }
        for(int i = 1; i < testGalleryManyElements.length - 1; i++) {
            assertNotNull(ds.getPrevPhoto());
        }
        assertNull(ds.getPrevPhoto());// end of history
        assertTrue(ds.getNextPhoto().equals(testGalleryManyElements[8]));// due to insertion order
    }


    /**
     *  Helper method to create a gallery of several photos
     */
    private DejaPhoto[] fillTestGallery() {
        DejaPhoto[] gallery = new DejaPhoto[11];
        calendar = Calendar.getInstance();

        for(int i = 0; i < 11; i++) {
          gallery[i] = new DejaPhoto(null, 0, 0, calendar.getTimeInMillis());
          //calendar.add(Calendar.HOUR, 1);
        }
        return gallery;
    }
}