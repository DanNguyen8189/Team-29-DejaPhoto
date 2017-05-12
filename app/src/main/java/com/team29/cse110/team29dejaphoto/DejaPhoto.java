package com.team29.cse110.team29dejaphoto;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by David Duplantier and Noah Lovato on 5/1/17.
 */

public class DejaPhoto implements Comparable<DejaPhoto> {

    private Uri photoUri;         /* Uri for this photo */
    private Calendar time;        /* This Calendar object will hold the time this photo was taken */
    private double latitude;      /* Lat and long coordinates where this photo was taken */
    private double longitude;

    private boolean karma;        /* Flags for karma, released, and whether the photo has been */
    private boolean released;     /* shown recently */
    private boolean showRecently;

    private int myScore;          /* Priority score of this photo */

    /**
     * Default constructor. All values are set to the Java default for their respective types.
     */
    public DejaPhoto() {}

    /**
     * DejaPhoto constructor. The photo's data including Uri, latitude, longitude, time taken,
     * and date taken, are passed as parameters to the constructor.
     */
    public DejaPhoto(Uri photoUri, double latitude, double longitude, Long time) {

        this.photoUri = photoUri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = new GregorianCalendar();
        this.time.setTimeInMillis(time);

        //updateScore(true, true, true);  /* By default, score is calculated with all settings true */
    }

    // TODO Don't think this constructor is needed anymore but unsure
    /*
     * DejaPhoto constuctor. This constructor call the regular constructor, then updates the
     * score for custom values of isLocationOn, isDateOn, and isTimeOn. This constructor is to
     * be used when creating new DejaPhoto objects when DejaPhoto settings are customized.
     */
//    public DejaPhoto(Uri photoUri, double latitude, double longitude, Long time, String date,
//                     boolean isLocationOn, boolean isDateOn, boolean isTimeOn) {
//
//        this(photoUri, latitude, longitude, time);  /* Add regular photo data . . . */
//        //updateScore(isLocationOn, isDateOn, isTimeOn);    /* ... and calculate custom score */
//
//    }

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

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLatitude(double lat){
        this.latitude = lat;
    }

    public void setLongitude(double lon){
        this.longitude = lon;
    }


    /* Score Calculation */

    // TODO Change to SharedPreferences

    /* Check which features of DejaVu Mode are on */
    private boolean isLocationOn = true;
    private boolean isDateOn = true;
    private boolean isTimeOn = true;

    /**
     * Updates the score for this DejaPhoto object, given settings for location, date, and time.
     */
    public void updateScore(Location location) {

        // TODO SharedPreferences
        int includeLocation = mapBooleanToInt(isLocationOn);
        int includeDate = mapBooleanToInt(isDateOn);
        int includeTime = mapBooleanToInt(isTimeOn);
        int recentlyViewed = mapBooleanToInt(isShownRecently());

        myScore = getKarmaPoints() - recentlyViewed +
                  includeLocation * getLocationPoints(location) +
                  includeDate * getDatePoints() +
                  includeTime * getTimeTakenPoints();
    }

    /* Private Score Calculation Helper Methods */

    private int getKarmaPoints() {
        return mapBooleanToInt(getKarma());
    }

    /**
     * photo is Dejaphoto to get points for
     * @returns 10 if location of photo is close to current location
     */
    private int getLocationPoints(Location location) {

        double distance = GpsMath.distanceBetween(
                location.getLatitude(), location.getLongitude(),
                getLatitude(), getLongitude()
        );

        return distance <= 1000 ? 10 : 0;
    }

    private int getTimeTakenPoints() {

        Calendar now = new GregorianCalendar();
        boolean notWithinTimeframe =
                Math.abs(getTime().get(Calendar.HOUR_OF_DAY)
                        - now.get(Calendar.HOUR_OF_DAY)) > 2 &&
                Math.abs(getTime().get(Calendar.HOUR_OF_DAY)
                        - now.get(Calendar.HOUR_OF_DAY)) < 22;

        return notWithinTimeframe ? 0 : 10;
    }

    private int getDatePoints() {

        Calendar now = new GregorianCalendar();
        boolean sameDayOfWeek =
                getTime().get(Calendar.DAY_OF_WEEK) == now.get(Calendar.DAY_OF_WEEK);

        return sameDayOfWeek ? 10 : 0;
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

}
