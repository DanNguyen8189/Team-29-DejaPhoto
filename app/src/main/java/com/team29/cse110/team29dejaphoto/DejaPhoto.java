package com.team29.cse110.team29dejaphoto;

import android.location.Location;
import android.net.Uri;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by David Duplantier and Noah Lovato on 5/1/17.
 */

public class DejaPhoto implements Comparable<DejaPhoto> {

    /* Photo Metadata */

    private Uri photoUri;         // Uri for this photo
    private Calendar time;        // This Calendar object will hold the time this photo was taken
    private Location location;    // Location object composing lat and long
                                  // coordinates where this photo was taken

    /* DejaPhoto properties */

    private boolean karma;        // Flags for karma, released, and whether the photo has been
    private boolean released;     // shown recently
    private boolean showRecently;

    private int myScore;          // Priority score of this photo

    /* Constants */

    private static final double METERS_TO_FEET = 3.28084;
    private static final int NEAR_RADIUS = 1000;
    private static final int MILLIS_IN_SEC = 1000;
    private static final int SCORE_UNIT = 10;


    /**
     * DejaPhoto constructor. The photo's data including Uri, latitude, longitude, time taken,
     * and date taken, are passed as parameters to the constructor.
     */
    public DejaPhoto(Uri photoUri, double latitude, double longitude, Long time) {

        this.photoUri = photoUri;
        this.time = new GregorianCalendar();
        this.time.setTimeInMillis(time * MILLIS_IN_SEC);
        this.location = new Location("");
        this.location.setLatitude(latitude);
        this.location.setLongitude(longitude);
    }

    /**
     * Used to compare to DejaPhoto objects by their priority score.
     * @param photo Photo to compare against this photo object.
     * @return For x.compareTo(y), this method returns:
     *         1   if x's score is greater than y's
     *         0   if x's score is equal to y's
     *         -1  if x's score is less than y's
     */
    public int compareTo(DejaPhoto photo) {

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


    /* Score Calculation */


    /**
     * Updates the score for this DejaPhoto object, given settings for location, date, and time.
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
        return mapBooleanToInt(getKarma());
    }

    /**
     * Calculates the score of this DejaPhoto based on location
     *
     * @returns SCORE_UNIT - If the location of the photo is close to the current location
     */
    private int getLocationPoints(Location location) {
        return (location.distanceTo(this.location) * METERS_TO_FEET) <= NEAR_RADIUS
                ? SCORE_UNIT : 0;
    }

    /**
     * Calculates the score of this DejaPhoto based on time
     *
     * @returns SCORE_UNIT - If the time when the photo was taken is close to the current time
     */
    private int getTimeTakenPoints() {

        Calendar lCalendar = Calendar.getInstance();
        lCalendar.setTime(getTime().getTime());
        lCalendar.add(Calendar.HOUR, -2);

        Calendar uCalendar = Calendar.getInstance();
        uCalendar.setTime(getTime().getTime());
        uCalendar.add(Calendar.HOUR, 2);

        Calendar now = new GregorianCalendar();
        Date currTime = now.getTime();

        boolean withinTimeFrame
                = currTime.after(lCalendar.getTime()) && currTime.before(uCalendar.getTime());

        return withinTimeFrame ? SCORE_UNIT : 0;
    }

    /**
     * Calculates the score of this DejaPhoto based on the day of the week
     *
     * @returns SCORE_UNIT - If the day that the photo was taken is the current day of the week
     */
    private int getDatePoints() {

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
    public int mapBooleanToInt(boolean value) {
        return (value) ? 1 : 0;
    }


    /* Getters and Setters */


    public Uri getPhotoUri() {
        return photoUri;
    }

    public boolean getKarma() {
        return karma;
    }

    public void setKarma() {
        karma = true;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased() {
        released = true;
    }

    public boolean isShownRecently() {
        return showRecently;
    }

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

}
