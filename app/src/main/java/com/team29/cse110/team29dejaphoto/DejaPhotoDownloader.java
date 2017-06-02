package com.team29.cse110.team29dejaphoto;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by David Duplantier on 6/2/17.
 */

public class DejaPhotoDownloader implements PhotoDownloader {

    Context context;

    public DejaPhotoDownloader(Context context) {
        this.context = context;
    }

    @Override
    public ArrayList<DejaPhoto> downloadAllPhotos() {

        ArrayList<DejaPhoto> allPhotos = new ArrayList<>();
        allPhotos.addAll( downloadMyPhotos() );
        allPhotos.addAll( downloadFriendsPhotos() );
        return allPhotos;

    }

    @Override
    public ArrayList<DejaPhoto> downloadMyPhotos() {

        PhotoLoader loader = new DejaPhotoLoader();
        DejaPhoto[] gallery = loader.getPhotosAsArray(context);
        return new ArrayList<>(Arrays.asList(gallery));

    }

    @Override
    public ArrayList<DejaPhoto> downloadFriendsPhotos() {
        /*
        ArrayList<DejaPhoto> friendsPhotos = new ArrayList<>();
        String[] friends = UserInfo.getFriends();

        for ( String friend : friends ) {
            // Do stuff to get friends photos from Firebase Adapter
        }
        */
        return null;
    }

}
