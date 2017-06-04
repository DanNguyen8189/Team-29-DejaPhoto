package com.team29.cse110.team29dejaphoto.models;

import android.location.Location;

import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Manages the photos to be shown to the user that is not in history
 */
public class Priorities {

    /* Priority Queue of DejaPhotos based on scores */
    private PriorityQueue<DejaPhoto> pq;

    /** Default Constructor */
    public Priorities() {
        pq = new PriorityQueue<>(10, Collections.<DejaPhoto>reverseOrder());
    }

    /**
     * Adds a photo to the Priorities object
     *
     * @param photo - The photo to be added into the sorted structure
     * @return True - If the photo is added
     *         False - Otherwise
     */
    public boolean add(DejaPhoto photo) {
        if(photo == null) return false;

        return pq.add(photo);
    }

    /**
     * Takes off the highest priority photo from the structure and returns it
     *
     * @return LocalPhoto - The LocalPhoto with the highest score
     */
    public DejaPhoto getNewPhoto(){
        return pq.poll();
    }

    /**
     * Updates the priorities of each LocalPhoto object in the structure
     *
     * @param location - The new current location to update score with respects to
     */
    public void updatePriorities(Location location, Preferences prefs) {
        // Naive implementation
        // create new temporary arraylist to hold dejaphoto objects for reshuffling
        ArrayList<DejaPhoto> temp = new ArrayList<>();

        // update each photo's score
        for (DejaPhoto photo : pq) {
            photo.updateScore(location, prefs);
            temp.add(photo);
        }

        // reset the original priorityqueue and re-add each photo from the temporary
        // arraylist to it
        pq = new PriorityQueue<>(10, Collections.<DejaPhoto>reverseOrder());
        for (DejaPhoto photo : temp) {
            pq.add(photo);
        }
    }

}
