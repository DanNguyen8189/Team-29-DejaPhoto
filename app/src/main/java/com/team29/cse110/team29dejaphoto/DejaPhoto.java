package com.team29.cse110.team29dejaphoto;

import android.net.Uri;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by David Duplantier and Noah Lovato on 5/1/17.
 */

public class DejaPhoto implements Comparable<DejaPhoto> {

    private Uri photoUri;         /* Uri for this photo */
    private double latitude;      /* Lat and long coordinates where this photo was taken */
    private double longitude;
    private Calendar time;        /* This Calendar object will hold the time this photo was taken */

    private boolean karma;        /* Flags for karma, released, and whether the photo has been */
    private boolean released;     /* shown recently */
    private boolean showRecently;

    private int myScore;          /* Priority score of this photo */

    /*
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

        karma = false;
        released = false;
        showRecently = false;

        //updateScore(true, true, true);  /* By default, score is calculated with all settings true */

    }

    /*
     * DejaPhoto constuctor. This constructor call the regular constructor, then updates the
     * score for custom values of isLocationOn, isDateOn, and isTimeOn. This constructor is to
     * be used when creating new DejaPhoto objects when DejaPhoto settings are customized.
     */
    public DejaPhoto(Uri photoUri, double latitude, double longitude, Long time, String date,
                     boolean isLocationOn, boolean isDateOn, boolean isTimeOn) {

        this(photoUri, latitude, longitude, time);  /* Add regular photo data . . . */
        //updateScore(isLocationOn, isDateOn, isTimeOn);    /* ... and calculate custom score */

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
     * Returns Uri
     */
    public Uri getPhotoUri() {
        return photoUri;
    }

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

    public void setTime(Calendar newTime)
    {
        this.time = newTime;
    }

    public Calendar getTime() {
        return time;
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
        this.latitude = lon;
    }


}
