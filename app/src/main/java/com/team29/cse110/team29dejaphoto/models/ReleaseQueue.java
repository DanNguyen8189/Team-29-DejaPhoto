package com.team29.cse110.team29dejaphoto.models;

import android.location.Location;

import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Created by David Duplantier on 6/3/17.
 */

public class ReleaseQueue {


    private PriorityQueue<DejaPhoto> photosToRelease;
    private final int INITIAL_SIZE = 10;


    public ReleaseQueue() {
        photosToRelease = new PriorityQueue<>(INITIAL_SIZE, Collections.<DejaPhoto>reverseOrder());
    }


    /* Check if photosToRelease has thisPhoto - if so, remove it and return true */
    public boolean hasThis(DejaPhoto thisPhoto) {

        DejaPhoto thatPhoto = photosToRelease.peek();

        if ( thisPhoto.equals(thatPhoto) ) {
            photosToRelease.poll();
            return true;
        }
        return false;
    }


    /* Update each photo in photosToRelease */
    public void updatePhotosToRelease(Location newLoc, Preferences newPrefs) {

        ArrayList<DejaPhoto> tempStorage = new ArrayList<>();

        for ( DejaPhoto photo : photosToRelease ) {
            photo.updateScore(newLoc, newPrefs);
            tempStorage.add(photo);
        }

        photosToRelease = new PriorityQueue<>(INITIAL_SIZE, Collections.<DejaPhoto>reverseOrder());
        photosToRelease.addAll(tempStorage);

    }

}
