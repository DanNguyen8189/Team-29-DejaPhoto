package com.team29.cse110.team29dejaphoto;

/**
 * Created by David Duplantier on 6/2/17.
 */

/*
 * This class represents a photo object eligible to be entered into a DisplayCycle that does not
 * contain any URI. Instead, this object is just a placeholder for photo data stored in Firebase
 * Storage, and contains a reference to the location of its corresponding photo in that storage.
 */
public class PlaceholderPhoto implements Photo {

    @Override
    public int compareTo(Photo photo) {
        return 0;
    }

    @Override
    public int getScore() {
        return 0;
    }
}
