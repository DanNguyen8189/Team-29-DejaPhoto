package com.team29.cse110.team29dejaphoto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.PriorityQueue;

public class Priorities {

    /* Check which features of DejaVu Mode are on */
    private boolean isLocationOn = true;
    private boolean isDateOn = true;
    private boolean isTimeOn = true;
    private double latitude; //current latitude of device
    private double longitude; //current longitude of device

    /* Priority Queue of DejaPhotos based on priority */
    private PriorityQueue<DejaPhoto> pq;

    public Priorities() {
        pq = new PriorityQueue<>(10, Collections.<DejaPhoto>reverseOrder());
    }

    /** add a photo to the Priorities object */
    public boolean add(DejaPhoto photo) {
        int score = this.calcScoreOf(photo);
        photo.setScore(score);
        return pq.add(photo);
    }

    /** take off highest priority photo from priority queue and return it */
    public DejaPhoto getNewPhoto(){
        return pq.poll();
    }

    /** Updates the priorities */
    public void updatePriorities() {
        // Naive implementation
        ArrayList<DejaPhoto> temp = new ArrayList<>();
        for(DejaPhoto photo : pq) {
            pq.remove(photo);
            temp.add(photo);
        }
        for(DejaPhoto photo : temp) {
            this.add(photo);
        }
    }


    /*
     * Updates the score for this DejaPhoto object, given settings for location, date, and time.
     */
    public int calcScoreOf(DejaPhoto photo) {

        int myScore = 0;
        int includeLocation = mapBooleanToInt(isLocationOn);
        int includeDate = mapBooleanToInt(isDateOn);
        int includeTime = mapBooleanToInt(isTimeOn);
        int recentlyViewed = mapBooleanToInt(photo.isShownRecently());

        myScore = getKarmaPoints(photo) - recentlyViewed +
                includeLocation * getLocationPoints(photo) +
                includeDate * getDatePoints(photo) +
                includeTime * getTimeTakenPoints(photo);

        return myScore;

    }


    // Helper methods below

    private int getKarmaPoints(DejaPhoto photo) {
        return mapBooleanToInt(photo.getKarma());
    }

    //photo is Dejaphoto to get points for and x,y are lat and long of current location
    //returns 10 if location of photo is close to current location
    private int getLocationPoints(DejaPhoto photo)
    {
        return 0;
    }

    private int getTimeTakenPoints(DejaPhoto photo) {

        Calendar now = new GregorianCalendar();

        if (Math.abs(photo.getTime().get(Calendar.HOUR_OF_DAY) - now.get(Calendar.HOUR_OF_DAY)) > 2 &&
                Math.abs(photo.getTime().get(Calendar.HOUR_OF_DAY) - now.get(Calendar.HOUR_OF_DAY)) < 22) {
            return 0;
        }
        else {
            return 10;
        }
    }

    private int getDatePoints(DejaPhoto photo) {

        Calendar now = new GregorianCalendar();

        if ( photo.getTime().get(Calendar.DAY_OF_WEEK) == now.get(Calendar.DAY_OF_WEEK) ) {
            return 10;
        }
        else {
            return 0;
        }
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
