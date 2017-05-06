package com.team29.cse110.team29dejaphoto;

import java.util.*;

/**
 * Created by Dan Nguyen Brian Orensztein on 5/2/2017.
 */

public class DisplayCycle {

    private class Priorities {

        // Check which features of DejaVu Mode are on
        boolean isLocationOn;
        boolean isDateOn;
        boolean isTimeOn;

        PriorityQueue<DejaPhoto> album; // Holds a sorted list of photos based on priority

        /* Priorities Default Constructor */
        private Priorities() {
            isLocationOn = true;
            isDateOn = true;
            isTimeOn = true;

            album = new PriorityQueue<DejaPhoto>(); // TODO*******Do we need to pass in the comparator???********
        }

        /* add a photo to the heap */
        private void addToHeap(DejaPhoto photo){
            album.add(photo);
        }

        /* take off highest priority photo from priority queue and return it */
        private DejaPhoto getNewPhoto(){
            return album.poll();
        }

    /* Updates the priorities
    public void updatePriorities() {
        TODO
    }
    */
    }

    private class History {

        LinkedList<DejaPhoto>  historyData; // Holds the history objects
        ListIterator<DejaPhoto> listIterator; // Holds current position in the history list
        int maxTen; // Keep track of how many photos are in the history
        //int counter; // Keep track of which photo in history we are on

        /* History Default Constructor */
        private History() {
            historyData = new LinkedList<DejaPhoto>();
            listIterator = historyData.listIterator();
            maxTen = 0;
            //counter = 0;
        }

        /**
         * Used to check if we are currently at the latest DejaPhoto in the history list.
         * param None
         * return boolean - False if the number of photos in the list equal the counter.
         *                   Otherwise, returns true.
         */
        private boolean checkValidNext() {
            //return (maxTen != counter);
            return listIterator.hasNext();
        }

        /**
         * Used to check if we are currently at the oldest DejaPhoto in the history list.
         * param None
         * return boolean - True if there are photos available when traversing backwards through
         *                  the history; false otherwise.
         */
        private boolean checkValidPrev() {
            if(listIterator.hasPrevious()) return true;
            return false;
        }

        /**
         * Used to increment the counter (which photo we are looking at in the history
         * list) and to get the next photo.
         * param None
         * return DejaPhoto - the photo to be displayed
         */
        private DejaPhoto getNext() {
            //counter++;
            return listIterator.next();
        }

        /**
         * Used to increment the iterator and to get the previous photo.
         * param None
         * return DejaPhoto - the photo to be displayed
         *        null - there are no previous photos available
         */
        private DejaPhoto getPrev() {
            if(!checkValidPrev()) return null;
            return listIterator.previous();
        }

        /**
         * Used to remove DejaPhoto's from the history list when it reaches 10
         * photos, and to add DejaPhoto's to the history when we swipe left.
         * @param photo - Photo to add from priority queue to history list
         * @return DejaPhoto - Photo to be added back into the PQ if removed from
         *                     the list
         */
        private DejaPhoto updateHistory(DejaPhoto photo) {
            DejaPhoto toMove = null; // Holds the photo removed from the list

            // List is at max capacity
            if (maxTen == 10) {
                // Remove the oldest photo from the list
                toMove = historyData.pollFirst();
                maxTen--;
            }

            // Add newest photo from PQ to the list
            historyData.add(photo);
            //update iterator to new index
            listIterator.next();
            maxTen++;

            return toMove;
        }
    }

    // Initialize member variables for DisplayCycle
    private History history;
    private Priorities priorities;
    //private boolean inHistory;

    /* DisplayCycle constructor */
    public DisplayCycle() {
        // Create an instance of the History and Priorities class
        history = new History();
        priorities = new Priorities();

        //inHistory = false; // Used to check if we are in the history list or not
    }

    /*
     * Add a single photo to album.
     */
    public void addToCycle(DejaPhoto photo) {
        this.priorities.addToHeap(photo);
    }

    /**
     * Used to get the next photo in the sequence, calling other helper methods to
     * determine where to get the next photo.
     * param none
     * return DejaPhoto - photo to be displayed
     */
    public DejaPhoto getNextPhoto() {

        DejaPhoto toDisplay = null; // Initialize the photo that will be displayed
        DejaPhoto toAdd = null; // Initialize the photo to be added to the PQ album

        // The photo we are looking at is not the latest photo in history
        if (history.checkValidNext()) {
            toDisplay = history.getNext();
            return toDisplay;
        }

        // We are no longer in the history list (now in PQ)
        else {
            //inHistory = false;
            toDisplay = priorities.getNewPhoto();
            toAdd = history.updateHistory(toDisplay);

            // history list was at max capacity and a photo was removed to be re-added to PQ
            if (toAdd != null) {
                priorities.addToHeap(toAdd);
            }
        }

        return toDisplay;
    }

    /**
     * Used to get the previous photo in the sequence, calling other helper methods to
     * determine where to get the previous photo.
     * param none
     * return DejaPhoto - photo to be displayed
     *        null - there are no previous photos available
     */
    public DejaPhoto getPrevPhoto() {
        if (history.checkValidPrev())
            return history.getPrev();
        return null;
    }

}
