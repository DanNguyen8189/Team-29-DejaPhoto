package com.team29.cse110.team29dejaphoto;

import android.net.Uri;
import android.util.Log;

import com.team29.cse110.team29dejaphoto.models.LocalPhoto;
import com.team29.cse110.team29dejaphoto.models.History;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tyler on 5/9/17.
 */
public class HistoryTest {

    LocalPhoto[] gallery = new LocalPhoto[10];
    LocalPhoto[] shortgallery = new LocalPhoto[] {new LocalPhoto(Uri.EMPTY,0,0,0L),
                                                new LocalPhoto(Uri.EMPTY,0,0,0L),
                                                new LocalPhoto(Uri.EMPTY,0,0,0L)};
    History history = new History();

    private String TAG = "HistoryTest";

    /**
     * A helper method to set up test objects before each test
     *
     */
    @Before
    public void setUp() {

        for(int i = 0; i < 10; i++) {

            gallery[i] = new LocalPhoto(Uri.EMPTY, 0, 0, Calendar.getInstance().getTimeInMillis());
        }
    }


    /**
     * Test that photos can be added into history.  addPhoto() returns the photo that was removed
     * from the end of the history.  If the history is not full, it will return null.
     *
     * @throws Exception
     */
    @Test
    public void addPhoto() throws Exception {

        assertNull(history.addPhoto(gallery[0]));// No item is returned from history
        for(LocalPhoto d: gallery) {
            history.addPhoto(d);
        }// 10 photos are added

        // this will kick the earliest item out of the history, as the gallery is ordered.
        for(LocalPhoto d: gallery) {
            assertTrue(history.addPhoto(d).equals(d));
        }
        Log.d(TAG,"Testing addPhoto() method");
    }


    /**
     * This tests that the previous photo in history (earlier in time) is properly returned
     *
     * @throws Exception
     */
    @Test
    public void getPrev() throws Exception {

        assertNull(history.getPrev());// Null when no history

        // fill the history
        for (LocalPhoto d: gallery) {
            history.addPhoto(d);
        }
        for(int i = 8; i >= 0; i-- ) {
            assertTrue(history.getPrev().equals(gallery[i]));
        }
        Log.d(TAG,"Testing getPrev() method");
    }


    /**
     * This tests that the next photo in history (newer in time) is properly returned
     *
     * @throws Exception
     */
    @Test
    public void getNext() throws Exception {

        // Next is null when iterator is at head of list
        assertNull(history.getNext());

        // fill the history
        for (LocalPhoto d: gallery) {
            history.addPhoto(d);
        }

        // Move to end of history
        for(LocalPhoto d:gallery) {
            history.getPrev();
        }

        assertNull(history.getPrev());// prev from end of history should be null
        assertTrue(history.getNext().equals(gallery[1]));// next should be second element in history

        Log.d(TAG,"Testing getNext() method");
    }


    /**
     * This tests that a photo is properly removed from the head of the list (earliest) and placed
     * at the end of the list (latest).  This can cycle when history is full, or partially full.
     *
     * @throws Exception
     */
    @Test
    public void cycle() throws Exception {

        assertNull(history.cycle());

        // fill the history
        for (LocalPhoto d: gallery) {
            history.addPhoto(d);
        }

        assertTrue(history.cycle().equals(gallery[0]));// Top of list is removed and returned
        for(int i = 1; i < gallery.length; i++) {
            assertTrue(history.cycle().equals(gallery[i]));
        }

        assertTrue(history.cycle().equals(gallery[0]));// history has cycled entire list

        // Test partially full history
        history = new History();

        for(LocalPhoto d: shortgallery) {
            history.addPhoto(d);
        }

        assertTrue(history.cycle().equals(shortgallery[0]));
        assertTrue(history.cycle().equals(shortgallery[1]));
        assertTrue(history.cycle().equals(shortgallery[2]));
        assertTrue(history.cycle().equals(shortgallery[0]));

        Log.d(TAG,"Testing cycle() method");
    }

}