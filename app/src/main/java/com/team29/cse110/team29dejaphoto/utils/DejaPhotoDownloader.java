package com.team29.cse110.team29dejaphoto.utils;

import android.content.Context;

import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoDownloader;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;

import com.team29.cse110.team29dejaphoto.models.Database;

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

        FirebasePhotosHelper helper = new FirebasePhotosHelper();
        return helper.downloadFriends();

    }

}
