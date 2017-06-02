package com.team29.cse110.team29dejaphoto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Duplantier on 6/2/17.
 */

/*
 * This class provides an interface for interacting with Firebase to return a list of DejaPhotos.
 * There are three methods: one to download all photos (friends and your photos), one to download
 * only your photos, and one to download only friends' photos.
 */
public interface PhotoDownloader {

    public List<DejaPhoto> downloadAllPhotos();

    public List<DejaPhoto> downloadMyPhotos();

    public List<DejaPhoto> downloadFriendsPhotos();

}
