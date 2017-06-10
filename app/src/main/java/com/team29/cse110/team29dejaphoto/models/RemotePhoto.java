package com.team29.cse110.team29dejaphoto.models;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;

import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by David Duplantier on 6/2/17.
 */

/*
 * This class represents a photo object eligible to be entered into a DisplayCycleMediator that does not
 * contain any URI. Instead, this object is just a placeholder for photo data stored in Firebase
 * Storage, and contains a reference to the location of its corresponding photo in that storage.
 */
public class RemotePhoto implements DejaPhoto {

    Bitmap bitmap;
    Uri photoUri;
    int myScore;
    int karma;
    double lat;
    double lng;
    boolean hasFriendKarma;
    boolean isShownRecently;
    private Calendar time = new GregorianCalendar();
    private Location location;
    private boolean released;

    String owner;
    String fileName;
    private String customLocation; // Holds the location that a user may set

    private static final double METERS_TO_FEET = 3.28084;
    private static final int NEAR_RADIUS = 1000;
    private final int SCORE_UNIT = 10;

    public RemotePhoto(Bitmap bitmap, int karma, double lat, double lng, long timeTaken, boolean released, String fileName) {
        this.bitmap = bitmap;
        this.karma = karma;
        this.lat = lat;
        this.lng = lng;
        this.released = released;
        time.setTimeInMillis(timeTaken);
        this.location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        this.fileName = fileName;
    }

    @Override
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

    @Override
    public int getScore() {
        return myScore;
    }

    public void setScore(int score) {
        this.myScore = score;
    }

    public int updateScore(Location location, Preferences prefs) {

        myScore = getKarma() - mapBooleanToInt(isShownRecently()) +
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

    public boolean hasKarma() { return hasFriendKarma; }

    public void setFriendKarma() { this.hasFriendKarma = true; }

    public int getKarma() {
        return karma;
    }

    public void addKarma() {
        if(!hasKarma()) {
            karma += 1;
            setFriendKarma();
        }
    }

    public Uri getPhotoUri(){
        return photoUri;
    }

    public boolean isShownRecently() {
        return isShownRecently;
    }

    private int mapBooleanToInt(boolean value) {
        return (value) ? 1 : 0;
    }

    public Calendar getTime() { return time; }

    public String getUniqueID() { return "";}

    public Location getLocation() { return location; }

    public void setReleased() { released = true; }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public void setCustomLocation(String customLocation) {
        this.customLocation = customLocation;
    }

    public String getCustomLocation() {
        return customLocation;
    }

    public String getOwner()
    {
        return this.owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }
}
