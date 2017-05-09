package com.team29.cse110.team29dejaphoto;

import android.net.Uri;

import org.junit.Before;
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

    // Create a few dummy Dejaphotos
    private DejaPhoto one = new DejaPhoto(null, 0, 0, 0L);
    private DejaPhoto two = new DejaPhoto(null, 0, 0, 0L);
    private DejaPhoto three = new DejaPhoto(null, 0, 0, 0L);

    // Create new DejaPhoto Galleries and populate them
    private DejaPhoto[] testGalleryEmpty = {};
    private DejaPhoto[] testGalleryOneElement = {one};
    private DejaPhoto[] testGalleryThreeElements = {one, two, three};
    private DejaPhoto[] testGalleryManyElements = fillTestGallery();


    @Test
    public void testFillDisplayCycle() throws Exception {

        // Test that empty display cycle is successfully filled
        assertTrue(ds.fillDisplayCycle(testGalleryEmpty));
        // Test that non-empty display cycle is successfully filled
        assertTrue(ds.fillDisplayCycle(testGalleryOneElement));
    }



    @Test
    public void addToCycle() throws Exception {

        // Test that photo is successfully added to an empty cycle
        ds.fillDisplayCycle(testGalleryEmpty);
        assertTrue(ds.addToCycle(new DejaPhoto(null, 0, 0, 0L)));

        // Test that photo is successfully added to a non-empty cycle
        ds.fillDisplayCycle(testGalleryOneElement);
        assertTrue(ds.addToCycle(new DejaPhoto(null, 0, 0, 0L)));
    }



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
        assertTrue(ds.getNextPhoto().equals(three));
        assertTrue(ds.getNextPhoto().equals(two));
        assertTrue(ds.getNextPhoto().equals(one)); // NOTE: We appear to be pulling from the wrong
                                                   //       direction in the array

        // Test get next on a full history set
        ds = new DisplayCycle();
        ds.fillDisplayCycle(testGalleryManyElements);
        ds.getNextPhoto();// NOTE: del this line and change loop index if we fix get next order
        for(int i = testGalleryManyElements.length - 1; i >= 0 ; i-- ) {
            assertTrue(ds.getNextPhoto().equals(testGalleryManyElements[i]) );
        }
    }



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
        assertTrue(ds.getPrevPhoto().equals(two));
        assertTrue(ds.getPrevPhoto().equals(three));
        assertTrue(ds.getPrevPhoto().equals(one));
        assertNull(ds.getPrevPhoto());// end of history


        // Test get prev on a full history set
        ds = new DisplayCycle();
        ds.fillDisplayCycle(testGalleryManyElements);
        ds.getNextPhoto();// NOTE: del this line and change loop indices if we fix get next order
        for(int i = testGalleryManyElements.length - 1; i >= 0 ; i-- ) {
            ds.getNextPhoto();
        }
        for(int i = 1; i < testGalleryManyElements.length - 1; i++) {
            assertNotNull(ds.getPrevPhoto());
        }
        assertNull(ds.getPrevPhoto());// end of history
        assertTrue(ds.getNextPhoto().equals(testGalleryManyElements[8]));// NOTE: Seems off?

    }


    // Helper method to create a gallery of several photos
    private DejaPhoto[] fillTestGallery() {
        DejaPhoto[] gallery = new DejaPhoto[11];
        for(int i = 0; i < 11; i++) {
          gallery[i] = new DejaPhoto(null, 0, 0, 0L);
        }
        return gallery;
    }
}