package com.team29.cse110.team29dejaphoto.models;

import android.location.Location;
import android.util.Log;

import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.HistoryStrategy;
import com.team29.cse110.team29dejaphoto.interfaces.PrioritiesStrategy;


/**
 * Manages the photos to be displayed on the homescreen
 */
public class DisplayCycleMediator {


    /* TAG to log debug statements for this class */
    private final static String TAG = "DisplayCycleMediator";

    /* Initialize member variables for DisplayCycleMediator */
    private HistoryStrategy history;
    private PrioritiesStrategy priorities;

    /** Default Constructor */
    public DisplayCycleMediator() {

        history = new LinkedListHistory();
        priorities = new PriorityQueuePriorities();
    }

    /** Overloaded Constructor */
    public DisplayCycleMediator(DejaPhoto[] gallery) {

        history = new LinkedListHistory();
        priorities = new PriorityQueuePriorities();

        for(DejaPhoto photo : gallery) {
            priorities.add(photo);
        }
    }

    /**
     * Add a single photo to the display cycle.
     *
     * @param photo - The LocalPhoto object to add to the DisplayCycleMediator
     * @return True - If the photo was added to the display cycle
     *         False - otherwise
     */
    public boolean addToCycle(DejaPhoto photo)
    {
        Log.d(TAG, "addToCycle (single photo)");
        return priorities.add(photo);
    }

    /**
     * Overloaded to add an array of LocalPhoto objects to the display cycle. This method
     * is intended to allow instantiating a DisplayCycleMediator object before an array of LocalPhoto
     * objects becomes available, and fill the DisplayCycleMediator at a later time. This is
     * useful so the app does not crash if the user presses the forwards/backwards button
     * before images are loaded.
     *
     * @param gallery - The input array of DejaPhotos to add to the DisplayCycleMediator
     * @return True - if any photo from the gallery was added to the DisplayCycleMediator
     *         False - otherwise
     */
    public boolean addToCycle(DejaPhoto[] gallery) {
        Log.d(TAG, "addToCycle (galley of photos)");

        // An empty gallery is valid to load from
        if(gallery == null) return true;

        for (DejaPhoto photo : gallery) {
            if(!priorities.add(photo)){
                return false;
            }
        }
        return true;
    }

    /**
     * Used to get the next photo in the sequence, calling other helper methods to
     * determine where to get the next photo.
     *
     * @return LocalPhoto - photo to be displayed
     */
    public DejaPhoto getNextPhoto() {

        Log.d(TAG, "getNextPhoto");


        DejaPhoto next = history.getNext();
        if(next == null) {
            DejaPhoto newPhoto = priorities.getNewPhoto();

            if(newPhoto != null) {
                DejaPhoto removed = history.addPhoto(newPhoto);
                if(removed != null) priorities.add(removed);

            } else {
                return history.cycle();
            }

            return newPhoto;
        }
        return next;
    }

    /**
     * Used to get the previous photo in the sequence, calling other helper methods to
     * determine where to get the previous photo.
     *
     * @return LocalPhoto - photo to be displayed
     *         null - there are no previous photos available
     */
    public DejaPhoto getPrevPhoto() {

        Log.d(TAG, "getPrevPhoto");


        return history.getPrev();
    }

    /**
     * Updates the priorities of each LocalPhoto in the DisplayCycleMediator.
     *
     * @param location - The location for which scores are to be calculated with respects to
     */
    public void updatePriorities(Location location, Preferences prefs) {

        Log.d(TAG, "updatePriorities");


        if(location == null) return;

        history.updatePriorities(location, prefs);
        priorities.updatePriorities(location, prefs);
    }

    public void removeCurrentPhoto() {
        history.removeFromHistory();
    }

}
