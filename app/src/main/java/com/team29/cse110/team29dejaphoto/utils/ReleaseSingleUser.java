package com.team29.cse110.team29dejaphoto.utils;

import android.content.SharedPreferences;

import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.ReleaseStrategy;
import com.team29.cse110.team29dejaphoto.models.DisplayCycleMediator;


/**
 * Created by David Duplantier on 5/20/17.
 */

/*
 * Implementation of ReleaseStrategy for a single user - all photos released are owned by the user.
 */
public class ReleaseSingleUser implements ReleaseStrategy {

    private DisplayCycleMediator displayCycle;
    private SharedPreferences sp;

    /* The contructor. The PhotoService needs to pass is the DisplayCycleMediator and the SharedPreferences */
    public ReleaseSingleUser(DisplayCycleMediator displayCycle, SharedPreferences sp) {
        this.displayCycle = displayCycle;
        this.sp = sp;
    }

    /*
     * Return 1 if photo is successfully released, and 0 if not.
     */
    @Override
    public int releasePhoto(DejaPhoto currPhoto) {

        /* If we got the current photo, record it is released and remove it from the DisplayCycleMediator */
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
        String photoid = "R_" + currPhoto.getUniqueID();

        /* Stores unique photo id */
        editor.putBoolean(photoid, true);
        editor.apply();
    }

}
