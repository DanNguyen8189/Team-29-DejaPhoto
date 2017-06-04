package com.team29.cse110.team29dejaphoto.utils;

import android.content.Context;

import com.team29.cse110.team29dejaphoto.interfaces.PhotoDownloader;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;
import com.team29.cse110.team29dejaphoto.models.LocalPhoto;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by David Duplantier on 6/2/17.
 */

public class DejaPhotoDownloader implements PhotoDownloader {

    Context context;

    public DejaPhotoDownloader(Context context) {
        this.context = context;
    }

    @Override
    public ArrayList<LocalPhoto> downloadAllPhotos() {

        ArrayList<LocalPhoto> allPhotos = new ArrayList<>();
        allPhotos.addAll( downloadMyPhotos() );
        allPhotos.addAll( downloadFriendsPhotos() );
        return allPhotos;

    }

    @Override
    public ArrayList<LocalPhoto> downloadMyPhotos() {

        PhotoLoader loader = new DejaPhotoLoader();
        LocalPhoto[] gallery = loader.getPhotosAsArray(context);
        return new ArrayList<>(Arrays.asList(gallery));

    }

    @Override
    public ArrayList<LocalPhoto> downloadFriendsPhotos() {
        /*
        ArrayList<LocalPhoto> friendsPhotos = new ArrayList<>();
        String[] friends = UserInfo.getFriends();

        for ( String friend : friends ) {
            // Do stuff to get friends photos from Firebase Adapter
        }
        */
        return null;
    }

}
