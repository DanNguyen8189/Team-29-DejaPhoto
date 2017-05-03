package com.team29.cse110.team29dejaphoto;

/**
 * Created by David Duplantier and Noah Lovato on 5/1/17.
 */

public class DejaPhoto implements Comparable<DejaPhoto> {

    private String photoPath;     /* Path to photo */

    private String latitude;      /* Lat and long coordinates where this photo was taken */
    private String longitude;

    private String time;          /* Time this photo was taken */

    private String date;          /* Day of the week this photo was taken */

    private boolean karma;        /* Flags for karma, released, and whether the photo has been */
    private boolean released;     /* shown recently */
    private boolean showRecently;

    // Score of this photo.
    private int myScore;

    /**
     * DejaPhoto constructor.
     */
    public DejaPhoto() {
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
     * @return Integer value of this DejaPhoto object's priority score.
     */
    public int calculateScore(boolean isLocationOn, boolean isDateOn, boolean isTimeOn) {
        return 0;
    }


    // Helper methods below

}
