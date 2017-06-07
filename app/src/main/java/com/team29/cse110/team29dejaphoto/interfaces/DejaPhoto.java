package com.team29.cse110.team29dejaphoto.interfaces;


import java.util.Calendar;
import android.location.Location;

import com.team29.cse110.team29dejaphoto.models.Preferences;

/**
 * Created by David Duplantier on 6/2/17.
 */

public interface DejaPhoto extends Comparable<DejaPhoto> {

    @Override
    public int compareTo(DejaPhoto dejaPhoto);

    public int getScore();

    public void addKarma();

    public boolean hasKarma();

    public Calendar getTime();

    public int updateScore(Location location, Preferences preferences);

    public String getUniqueID();

    public Location getLocation();

    public int getKarma();

    public void setReleased();

    public void setCustomLocation(String customLocation);

    public String getCustomLocation();

    // TODO Add all other necessary classes common to LocalPhoto and RemotePhoto
}
