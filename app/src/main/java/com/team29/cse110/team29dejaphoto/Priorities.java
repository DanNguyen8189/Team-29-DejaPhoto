package com.team29.cse110.team29dejaphoto;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Priorities {

    /* Check which features of DejaVu Mode are on */
    private boolean isLocationOn;
    private boolean isDateOn;
    private boolean isTimeOn;

    /* Priority Queue of DejaPhotos based on priority */
    private PriorityQueue<DejaPhoto> pq;

    public Priorities() {
        pq = new PriorityQueue<>();
    }

    /** add a photo to the Priorities object */
    public boolean add(DejaPhoto photo){
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
            photo.updateScore(isLocationOn, isDateOn, isTimeOn);
            temp.add(photo);
        }
        for(DejaPhoto photo : temp) {
            pq.add(photo);
        }
    }

}
