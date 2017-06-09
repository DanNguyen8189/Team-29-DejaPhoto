package com.team29.cse110.team29dejaphoto.interfaces;

import android.location.Location;

import com.team29.cse110.team29dejaphoto.models.Preferences;

/**
 * Created by DanXa on 5/25/2017.
 */

public interface HistoryStrategy {

    boolean isHistoryEmpty();

    boolean checkValidNext();

    boolean checkValidPrev();

    void getPrev();

    DejaPhoto getNext();

    DejaPhoto addPhoto(DejaPhoto photo);

    DejaPhoto cycle();

    void updatePriorities(Location location, Preferences prefs);

    void removeFromHistory();
}
