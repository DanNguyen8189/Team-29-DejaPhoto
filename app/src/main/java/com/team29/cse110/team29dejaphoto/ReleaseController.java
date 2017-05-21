package com.team29.cse110.team29dejaphoto;

import android.content.SharedPreferences;


/**
 * Created by David Duplantier on 5/20/17.
 */

public class ReleaseController {

    /*
     * Class Philosophy:
     *
     * PhotoService manages user interactions with the UI, and prefers delegating low level tasks
     * to other classes - it only cares when and where the user clicks. ReleaseController handles
     * the sordid responsibilities of releasing photos from the DisplayCycle and the controlled
     * chaos of notifying other users through the cloud when a shared photo is released.
     *
     * This class is intended to be composed within PhotoService, and since PhotoService just cares
     * when and where a user clicks on the UI, ReleaseController exposes only one public method:
     * releasePhoto(). Any concomitant responsibilities are kept private, so as not to disturb the
     * service, who God knows has enough to worry about already.
     */


    private DisplayCycle displayCycle;
    private SharedPreferences sp;

    /* The contructor. The PhotoService needs to pass is the DisplayCycle and the SharedPreferences */
    public ReleaseController(DisplayCycle displayCycle, SharedPreferences sp) {
        this.displayCycle = displayCycle;
        this.sp = sp;
    }

    public void releasePhoto() {

        /* Get currently displayed photo */
        DejaPhoto currPhoto = displayCycle.getCurrentPhoto();

        /* If we got the current photo, record it is released and remove it from the DisplayCycle */
        if (currPhoto != null) {
            currPhoto.setReleased();
            recordIsReleasedInPrefs(currPhoto);
            displayCycle.removeCurrentPhoto();
        }

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
