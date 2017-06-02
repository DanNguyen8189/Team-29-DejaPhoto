package com.team29.cse110.team29dejaphoto.interfaces;


/**
 * Created by David Duplantier on 6/2/17.
 */

public interface Photo extends Comparable<Photo> {

    @Override
    public int compareTo(Photo photo);

    public int getScore();

    // TODO Add all other neccessary classes common to DejaPhoto and PlaceholderPhoto
}
