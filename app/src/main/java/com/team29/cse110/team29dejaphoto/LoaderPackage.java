package com.team29.cse110.team29dejaphoto;

import android.provider.MediaStore;

/**
 * Created by David Duplantier on 5/6/17.
 */

public class LoaderPackage {

    // Get these pieces of data for each photo in the gallery database
    private final String[] projection = { MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.LATITUDE,
            MediaStore.Images.Media.LONGITUDE,
            MediaStore.Images.Media.DATE_ADDED };

    // Array of DejaPhoto objects for all images in the gallery
    private DejaPhoto[] gallery;

    public void initializeGallery(int size) {
        gallery = new DejaPhoto[size];
    }

    public DejaPhoto[] getGallery() {
        return gallery;
    }

    public String[] getProjection() {
        return projection;
    }

}
