package com.team29.cse110.team29dejaphoto.interfaces;

import android.location.Location;

import com.team29.cse110.team29dejaphoto.models.Preferences;

/**
 * Created by DanXa on 5/25/2017.
 */

public interface PrioritiesStrategy {
    boolean add(DejaPhoto photo);

    DejaPhoto getNewPhoto();

    void updatePriorities(Location location, Preferences prefs);
}
