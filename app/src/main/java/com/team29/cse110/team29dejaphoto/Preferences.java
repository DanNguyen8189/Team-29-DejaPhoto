package com.team29.cse110.team29dejaphoto;

/**
 * Created by Wis on 5/13/2017.
 */

public class Preferences {

    private boolean isLocationOn;
    private boolean isDateOn;
    private boolean isTimeOn;

    public Preferences(boolean isLocationOn, boolean isDateOn, boolean isTimeOn) {
        this.isLocationOn = isLocationOn;
        this.isDateOn = isDateOn;
        this.isTimeOn = isTimeOn;
    }

    public boolean isLocationOn() {
        return isLocationOn;
    }

    public boolean isDateOn() {
        return isDateOn;
    }

    public boolean isTimeOn() {
        return isTimeOn;
    }
}
