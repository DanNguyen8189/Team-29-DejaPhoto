package com.team29.cse110.team29dejaphoto;

import android.content.SharedPreferences;


/**
 * Created by David Duplantier on 5/20/17.
 */

/*
 * Implementation of ReleaseStrategy for a single user - all photos released are owned by the user.
 */
public class ReleaseSingleUser implements ReleaseStrategy {

    private DisplayCycle displayCycle;
    private SharedPreferences sp;

    /* The contructor. The PhotoService needs to pass is the DisplayCycle and the SharedPreferences */
    public ReleaseSingleUser(DisplayCycle displayCycle, SharedPreferences sp) {
        this.displayCycle = displayCycle;
        this.sp = sp;
    }

    /*
     * Return 1 if photo is successfully released, and 0 if not.
     */
    @Override
    public int releasePhoto() {

        /* Get currently displayed photo */
        DejaPhoto currPhoto = displayCycle.getCurrentPhoto();

        /* If we got the current photo, record it is released and remove it from the DisplayCycle */
        if (currPhoto != null) {
            currPhoto.setReleased();
            recordIsReleasedInPrefs(currPhoto);
            displayCycle.removeCurrentPhoto();
            return 1;
        }

        return 0;
    }


    /* This method handles all recording a photo is released in SharedPreferences */
    private void recordIsReleasedInPrefs(DejaPhoto currPhoto) {

        /* Create editor for storing unique photoids */
        SharedPreferences.Editor editor = sp.edit();

        /* Unique photoid given to a photo that has been released */
        String photoid = Long.toString(currPhoto.getTime().getTimeInMillis()/1000) + "0" + "1"
                + currPhoto.getPhotoUri();

        /* Stores unique photo id */
        editor.putString(photoid, "Release Photo");
        editor.apply();

    }

}
