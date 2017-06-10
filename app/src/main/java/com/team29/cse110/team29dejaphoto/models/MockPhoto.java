package com.team29.cse110.team29dejaphoto.models;

import android.location.Location;
import android.net.Uri;

import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by DanXa on 6/9/2017.
 */

public class MockPhoto implements DejaPhoto {

    private Uri photoUri;         // Uri for this photo
    private Calendar time;        // This Calendar object will hold the time this photo was taken
    private Location location;    // Location object composing lat and long
    // coordinates where this photo was taken
    private String customLocation; // Holds the location that a user may set

    private final int SCORE_UNIT = 10;
    private static final double METERS_TO_FEET = 3.28084;
    private static final int NEAR_RADIUS = 1000;

    /* LocalPhoto properties */

    private int karma;        // Flags for karma, released, and whether the photo has been
    private boolean karmaFromUser;
    private boolean released;     // shown recently
    private boolean showRecently;

    private int myScore;          // Priority score of this photo

    public MockPhoto(Uri photoUri, double latitude, double longitude, Long time, String customUserLocation) {

        this.photoUri = photoUri;
        this.time = new GregorianCalendar();
        this.time.setTimeInMillis(time);
        this.location = new Location("");
        this.location.setLatitude(latitude);
        this.location.setLongitude(longitude);
        this.customLocation = customUserLocation;
        this.karma = 0;
        this.karmaFromUser = false;
    }

    public int compareTo(DejaPhoto dejaPhoto) {

        int theirScore = dejaPhoto.getScore();

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


    /* Score Calculation */


    /**
     * Updates the score for this LocalPhoto object, given settings for location, date, and time.
     *
     * @param location - The current location of the user/device
     *        prefs    - The DejaVu Mode preferences current enabled by the user
     */
    public int updateScore(Location location, Preferences prefs) {

        myScore = getKarmaPoints() - mapBooleanToInt(isShownRecently()) +
                mapBooleanToInt(prefs.isLocationOn()) * getLocationPoints(location) +
                mapBooleanToInt(prefs.isDateOn()) * getDatePoints() +
                mapBooleanToInt(prefs.isTimeOn()) * getTimeTakenPoints();

        return myScore;
    }


    /* Private Score Calculation Helper Methods */


    private int getKarmaPoints() {
        return getKarma();
    }

    /**
     * Calculates the score of this LocalPhoto based on location
     *
     * @returns SCORE_UNIT - If the location of the photo is close to the current location
     */
    public int getLocationPoints(Location location) {
        return (location.distanceTo(this.location) * METERS_TO_FEET) <= NEAR_RADIUS
                ? SCORE_UNIT : 0;

    }

    /**
     * Calculates the score of this LocalPhoto based on time
     *
     * @returns SCORE_UNIT - If the time when the photo was taken is close to the current time
     */
    public int getTimeTakenPoints() {

        Calendar lCalendar = Calendar.getInstance();
        lCalendar.setTime(getTime().getTime());
        lCalendar.set(1, 1, 1);
        lCalendar.add(Calendar.HOUR, -2);

        Calendar uCalendar = Calendar.getInstance();
        uCalendar.setTime(getTime().getTime());
        uCalendar.set(1, 1, 1);
        uCalendar.add(Calendar.HOUR, 2);

        Calendar now = new GregorianCalendar();
        now.set(1, 1, 1);
        Date currTime = now.getTime();

        boolean withinTimeFrame
                = currTime.after(lCalendar.getTime()) && currTime.before(uCalendar.getTime());

        return withinTimeFrame ? SCORE_UNIT : 0;

    }

    /**
     * Calculates the score of this LocalPhoto based on the day of the week
     *
     * @returns SCORE_UNIT - If the day that the photo was taken is the current day of the week
     */
    public int getDatePoints() {

        Calendar now = new GregorianCalendar();
        boolean sameDayOfWeek =
                getTime().get(Calendar.DAY_OF_WEEK) == now.get(Calendar.DAY_OF_WEEK);

        return sameDayOfWeek ? SCORE_UNIT : 0;

    }

    /*
     * To avoid a large, confusing if-else structure in the calculateScore method, simply
     * use this method to map the boolean values to 0 (false) and 1 (true). Multiply these
     * values with scores, so that multiplying by zero ignores a score, and 1 includes the
     * score.
     */
    private int mapBooleanToInt(boolean value) {
        return (value) ? 1 : 0;
    }


    /* Getters and Setters */

    public String getUniqueID() {return photoUri.toString(); }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public Uri setPhotoUri( Uri uri ){
        photoUri = uri;
        return photoUri;
    }

    public int getKarma() {
        return karma;
    }

    public void addKarma() {

        if(!karmaFromUser) {
            karma +=1;
            karmaFromUser = true;
        }
    }

    public boolean hasKarma() { return karmaFromUser; }

    public int setkarma(int newKarma){
        karma = newKarma;
        return karma;
    }

    public void setKarmaFromUser(boolean karmaFromUser) { this.karmaFromUser = karmaFromUser; }

    public boolean isReleased() {
        return released;
    }

    public void setReleased() {
        released = true;
    }

    public boolean isShownRecently() { return showRecently; }

    public void setShowRecently() {
        showRecently = true;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar newTime)
    {
        this.time = newTime;
    }

    public int getScore() {
        return myScore;
    }

    public void setScore(int newScore) {
        myScore = newScore;
    }

    public Location getLocation(){
        return location;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public void setCustomLocation(String customLocation) {
        this.customLocation = customLocation;
    }

    public String getCustomLocation() {
        return customLocation;
    }
}
