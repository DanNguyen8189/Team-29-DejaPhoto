package com.team29.cse110.team29dejaphoto;

import android.location.Location;

public class DisplayCycle {

    /* Initialize member variables for DisplayCycle */
    private History history;
    private Priorities priorities;

    /** Default Constructor */
    public DisplayCycle() {
        history = new History();
        priorities = new Priorities();
    }

    /** Overloaded Constructor */
    public DisplayCycle(DejaPhoto[] gallery) {
        history = new History();
        priorities = new Priorities();

        for(DejaPhoto photo : gallery) {
            priorities.add(photo);
        }
    }

    /*
     * Fill the DisplayCycle object with an array of DejaPhoto objects. This method is intended
     * to allow instantiating a DisplayCycle object before an array of DejaPhoto objects becomes
     * available, and fill the DisplayCycle at a later time. This is useful so the app does
     * not crash if the user presses the forwards/backwards button before images are loaded.
     */
    public boolean fillDisplayCycle(DejaPhoto[] gallery) {

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
     * Add a single photo to album.
     */
    public boolean addToCycle(DejaPhoto photo) {
        return priorities.add(photo);
    }

    /**
     * Used to get the next photo in the sequence, calling other helper methods to
     * determine where to get the next photo.
     * @return DejaPhoto - photo to be displayed
     */
    public DejaPhoto getNextPhoto() {
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
     * @return DejaPhoto - photo to be displayed
     *         null - there are no previous photos available
     */
    public DejaPhoto getPrevPhoto() {
        return history.getPrev();
    }

    /**
     * Updates the priorities of each DejaPhoto in priorities.
     */
    public void updatePriorities(Location location) {
        priorities.updatePriorities(location);
    }

}
