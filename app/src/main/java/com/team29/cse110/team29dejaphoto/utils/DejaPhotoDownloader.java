package com.team29.cse110.team29dejaphoto.utils;

import android.content.Context;

import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoDownloader;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;
import com.team29.cse110.team29dejaphoto.models.LocalPhoto;
import com.team29.cse110.team29dejaphoto.models.User;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by David Duplantier on 6/2/17.
 */

public class DejaPhotoDownloader implements PhotoDownloader {

    Context context;
    User user;

    public DejaPhotoDownloader(Context context) {
        this.context = context;
        this.user = new User();
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

        String[] friends = user.getFriends();
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
