package com.team29.cse110.team29dejaphoto.interfaces;

/**
 * Created by David Duplantier on 5/22/17.
 */

/*
 * Implementation of the release functionality using the Strategy Pattern. Implementations of
 * ReleaseStrategy are one of a family of algorithms for releasing photos.
 */
public interface ReleaseStrategy {

    /*
     * This method releases the photo currently displayed on the home screen.
     * Return 1 if photo was successfully released, and 0 if not.
     */
    int releasePhoto(DejaPhoto currPhoto);

}
