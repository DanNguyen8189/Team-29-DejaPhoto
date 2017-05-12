package com.team29.cse110.team29dejaphoto;

import android.location.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Priorities {

    /* Priority Queue of DejaPhotos based on priority */
    private PriorityQueue<DejaPhoto> pq;

    /** Default Constructor */
    public Priorities() {
        pq = new PriorityQueue<>(10, Collections.<DejaPhoto>reverseOrder());
    }

    /** add a photo to the Priorities object */
    public boolean add(DejaPhoto photo) {
        return pq.add(photo);
    }

    /** take off highest priority photo from priority queue and return it */
    public DejaPhoto getNewPhoto(){
        return pq.poll();
    }

    /** Updates the priorities */
    public void updatePriorities(Location location) {
        // Naive implementation
        ArrayList<DejaPhoto> temp = new ArrayList<>();
        for (DejaPhoto photo : pq) {
            photo.updateScore(location);
            temp.add(photo);
        }
        pq = new PriorityQueue<>(10, Collections.<DejaPhoto>reverseOrder());
        for (DejaPhoto photo : temp) {
            this.add(photo);
        }
    }

}
