package com.team29.cse110.team29dejaphoto.models;

import android.location.Location;


/**
 * Manages the photos to be displayed on the homescreen
 */
public class DisplayCycle {


    /* TAG to log debug statements for this class */
    private final static String TAG = "DisplayCycle";

    /* Initialize member variables for DisplayCycle */
    private History history;
    private Priorities priorities;

    /** Default Constructor */
    public DisplayCycle() {

        history = new History();
        priorities = new Priorities();
    }

    /** Overloaded Constructor */
    public DisplayCycle(LocalPhoto[] gallery) {

        history = new History();
        priorities = new Priorities();

        for(LocalPhoto photo : gallery) {
            priorities.add(photo);
        }
    }

    /**
     * Add a single photo to the display cycle.
     *
     * @param photo - The LocalPhoto object to add to the DisplayCycle
     * @return True - If the photo was added to the display cycle
     *         False - otherwise
     */
    public boolean addToCycle(LocalPhoto photo) {
        return priorities.add(photo);
    }

    /**
     * Overloaded to add an array of LocalPhoto objects to the display cycle. This method
     * is intended to allow instantiating a DisplayCycle object before an array of LocalPhoto
     * objects becomes available, and fill the DisplayCycle at a later time. This is
     * useful so the app does not crash if the user presses the forwards/backwards button
     * before images are loaded.
     *
     * @param gallery - The input array of DejaPhotos to add to the DisplayCycle
     * @return True - if any photo from the gallery was added to the DisplayCycle
     *         False - otherwise
     */
    public boolean addToCycle(LocalPhoto[] gallery) {

        // An empty gallery is valid to load from
        if(gallery == null) return true;

        for (LocalPhoto photo : gallery) {
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
    public LocalPhoto getNextPhoto() {
        LocalPhoto next = history.getNext();
        if(next == null) {
            LocalPhoto newPhoto = priorities.getNewPhoto();

            if(newPhoto != null) {
                LocalPhoto removed = history.addPhoto(newPhoto);
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
    public LocalPhoto getPrevPhoto() {
        return history.getPrev();
    }

    /**
     * Updates the priorities of each LocalPhoto in the DisplayCycle.
     *
     * @param location - The location for which scores are to be calculated with respects to
     */
    public void updatePriorities(Location location, Preferences prefs) {
        if(location == null) return;

        history.updatePriorities(location, prefs);
        priorities.updatePriorities(location, prefs);
    }

    public void removeCurrentPhoto() {
        history.removeFromHistory();
    }

    public LocalPhoto getCurrentPhoto() {
        return history.getCurrentPhoto();
    }

}
