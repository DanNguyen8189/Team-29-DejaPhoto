package com.team29.cse110.team29dejaphoto.models;

import android.graphics.Bitmap;
import android.location.Location;

import com.team29.cse110.team29dejaphoto.interfaces.Photo;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by David Duplantier on 6/2/17.
 */

/*
 * This class represents a photo object eligible to be entered into a DisplayCycle that does not
 * contain any URI. Instead, this object is just a placeholder for photo data stored in Firebase
 * Storage, and contains a reference to the location of its corresponding photo in that storage.
 */
public class FriendsPhoto implements Photo {

    Bitmap photo;
    int myScore;
    int karma;
    double lat;
    double lng;
    boolean isShownRecently;
    private Calendar time;
    private Location location;

    private static final double METERS_TO_FEET = 3.28084;
    private static final int NEAR_RADIUS = 1000;
    private final int SCORE_UNIT = 10;

    public FriendsPhoto(Bitmap photo, int karma, double lat, double lng) {
        this.photo = photo;
        this.karma = karma;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public int compareTo(Photo photo) {

        int theirScore = photo.getScore();

        if ( myScore > theirScore ) {
            return 1;
        }
        else if ( myScore == theirScore ) {
            return 0;
        }
        else {
            return -1;
        }
    }

    @Override
    public int getScore() {
        return myScore;
    }

    public int updateScore(Location location, Preferences prefs) {

        myScore = getKarmaPoints() - mapBooleanToInt(isShownRecently()) +
                mapBooleanToInt(prefs.isLocationOn()) * getLocationPoints(location) +
                mapBooleanToInt(prefs.isDateOn()) * getDatePoints() +
                mapBooleanToInt(prefs.isTimeOn()) * getTimeTakenPoints();

        return myScore;
    }

    public int getLocationPoints(Location location) {
        return (location.distanceTo(this.location) * METERS_TO_FEET) <= NEAR_RADIUS
                ? SCORE_UNIT : 0;
    }

    public int getTimeTakenPoints() {

        Calendar lCalendar = Calendar.getInstance();
        lCalendar.setTime(time.getTime());
        lCalendar.set(1, 1, 1);
        lCalendar.add(Calendar.HOUR, -2);

        Calendar uCalendar = Calendar.getInstance();
        uCalendar.setTime(time.getTime());
        uCalendar.set(1, 1, 1);
        uCalendar.add(Calendar.HOUR, 2);

        Calendar now = new GregorianCalendar();
        now.set(1, 1, 1);
        Date currTime = now.getTime();

        boolean withinTimeFrame
                = currTime.after(lCalendar.getTime()) && currTime.before(uCalendar.getTime());

        return withinTimeFrame ? SCORE_UNIT : 0;
    }

    public int getDatePoints() {

        Calendar now = new GregorianCalendar();
        boolean sameDayOfWeek =
                time.get(Calendar.DAY_OF_WEEK) == now.get(Calendar.DAY_OF_WEEK);

        return sameDayOfWeek ? SCORE_UNIT : 0;
    }

    public int getKarmaPoints() {
        return karma;
    }

    public boolean isShownRecently() {
        return isShownRecently;
    }

    private int mapBooleanToInt(boolean value) {
        return (value) ? 1 : 0;
    }

}
