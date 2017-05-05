package com.team29.cse110.team29dejaphoto;

import android.net.Uri;

/**
 * Created by David Duplantier and Noah Lovato on 5/1/17.
 */

public class DejaPhoto implements Comparable<DejaPhoto> {

    private Uri photoUri;         /* Uri for this photo */
    private double latitude;      /* Lat and long coordinates where this photo was taken */
    private double longitude;
    private Long time;            /* Time this photo was taken (seconds since January 1st 1970 */
    private String date;          /* Day of the week this photo was taken */

    // TODO There's are variety of other useful data like orientation, height, etc. that might become useful later on.

    private boolean karma;        /* Flags for karma, released, and whether the photo has been */
    private boolean released;     /* shown recently */
    private boolean showRecently;

    private int myScore;          /* Priority score of this photo */

    /**
     * DejaPhoto constructor. The photo's data including Uri, latitude, longitude, time taken,
     * and date taken, are passed as parameters to the constructor.
     */
    public DejaPhoto(Uri photoUri, double latitude, double longitude, Long time, String date) {

        this.photoUri = photoUri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.date = date;

        karma = false;
        released = false;
        showRecently = false;

        myScore = 0;
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


    // Getters and Setters


    /**
     * Returns karma of this DejaPhoto object.
     * @return karma (true or false).
     */
    public boolean getKarma() {
        return karma;
    }

    /**
     * Set karma flag to true.
     */
    public void setKarma() {
        karma = true;
    }

    /**
     * Returns released flag of this DejaPhoto object.
     * @return released (true or false).
     */
    public boolean isReleased() {
        return released;
    }

    /**
     * Set release flag to true;
     */
    public void setReleased() {
        released = true;
    }

    /**
     * Returns shownRecently flag of this DejaPhoto object.
     * @return shownRecently (true or false).
     */
    public boolean isShownRecently() {
        return showRecently;
    }
    /**
     * Set showRecently flag to true.
     */
    public void setShowRecently() {
        showRecently = true;
    }

    public int getScore() {
        return myScore;
    }

    public void setScore(int newScore) {
        myScore = newScore;
    }

    /**
     * Calculates the priority score of this DejaPhoto object given settings to include/ignore
     * location, date, and time.
     * @param isLocationOn True to include location data in score calculation, and false otherwise.
     * @param isDateOn True to include date in score calculation, and false otherwise.
     * @param isTimeOn True to inlcude time in score calculation, and false otherwise.
     * @return int value of this DejaPhoto object's priority score.
     */
    public int calculateScore(boolean isLocationOn, boolean isDateOn, boolean isTimeOn) {

        int includeLocation = mapBooleanToInt(isLocationOn);
        int includeDate = mapBooleanToInt(isDateOn);
        int includeTime = mapBooleanToInt(isTimeOn);

        return getKarmaPoints() +
               includeLocation * getLocationPoints() +
               includeDate * getDatePoints() +
               includeTime * getTimeTakenPoints();

    }


    // Helper methods below

    private int getKarmaPoints() {
        return mapBooleanToInt(karma);
    }

    private int getLocationPoints() {
        return 0;
    }

    private int getTimeTakenPoints() {
        return 0;
    }

    private int getDatePoints() {
        return 0;
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
