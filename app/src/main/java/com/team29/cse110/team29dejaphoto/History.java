package com.team29.cse110.team29dejaphoto;

import android.location.Location;

import java.util.LinkedList;
import java.util.ListIterator;

public class History {

    private LinkedList<DejaPhoto> historyList;
    private ListIterator<DejaPhoto> iterator;
    private boolean forward;
    private int nelems;

    /** Default Constructor */
    public History() {
        historyList = new LinkedList<>(); //new LinkedList<DejaPhoto>();
        iterator = historyList.listIterator();
        forward = true;
    }

    /**
     * Used to check if we are currently at the latest DejaPhoto in the history list.
     * @return boolean - False if the number of photos in the list equal the counter.
     *                   Otherwise, returns true.
     */
    private boolean checkValidNext() {
        return iterator.hasPrevious();
    }

    /**
     * Used to check if we are currently at the oldest DejaPhoto in the history list.
     * @return boolean - True if there are photos available when traversing backwards through
     *                   the history; false otherwise.
     */
    private boolean checkValidPrev() {
        return iterator.hasNext();
    }

    /**
     * Used to increment the counter (which photo we are looking at in the history
     * list) and to get the next photo.
     * @return DejaPhoto - the photo to be displayed
     */
    public DejaPhoto getNext() {
        if(!forward && checkValidNext()) {
            iterator.previous();
            forward = true;
        }
        return checkValidNext() ? iterator.previous() : null;
    }

    /**
     * Used to increment the iterator and to get the previous photo.
     * @return DejaPhoto - the photo to be displayed
     *         null - there are no previous photos available
     */
    public DejaPhoto getPrev() {
        if(forward && checkValidPrev()) {
            iterator.next();
            forward = false;
        }
        return checkValidPrev() ? iterator.next() : null;
    }

    /**
     * Used to remove DejaPhoto's from the history list when it reaches 10
     * photos, and to add DejaPhoto's to the history when we swipe left.
     * @param photo - Photo to add from priority queue to history list
     * @return DejaPhoto - Photo to be added back into the PQ if removed from
     *                     the list
     */
    public DejaPhoto addPhoto(DejaPhoto photo) {
        DejaPhoto removed = null;

        if(nelems == 10) {
            removed = historyList.removeLast();
        } else {
            nelems ++;
        }

        historyList.addFirst(photo);
        iterator = historyList.listIterator();

        return removed;
    }

    /**
     * Cycles the last photo in the history towards the front
     * @return DejaPhoto cycled back to the front of the list
     */
    public DejaPhoto cycle() {
        if(nelems != 0) {
            DejaPhoto toCycle = historyList.removeLast();
            historyList.addFirst(toCycle);
            iterator = historyList.listIterator();
            return toCycle;
        }
        return null;
    }

    /**
     * Update the priorites of all DejaPhoto Objects held by this History structure
     * @param location The new current location to update score with respects to
     */
    public void updatePriorities(Location location) {
        for(DejaPhoto photo : historyList) {
            photo.updateScore(location);
        }
    }

}