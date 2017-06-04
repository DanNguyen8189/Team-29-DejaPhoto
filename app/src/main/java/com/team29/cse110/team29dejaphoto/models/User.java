package com.team29.cse110.team29dejaphoto.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team29.cse110.team29dejaphoto.interfaces.Photo;

import java.util.ArrayList;

/**
 * Created by David Duplantier on 6/3/17.
 */

public class User {

    FirebaseDatabase database;
    DatabaseReference myRef;


    /*
     * Constructor. Get reference to the Firebase realtime database.
     */
    public User() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    public String[] getFriends() {

        ArrayList<Photo> friendsPhotos = new ArrayList<>();

        return null;
    }
}
