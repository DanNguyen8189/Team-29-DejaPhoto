package com.team29.cse110.team29dejaphoto.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by David Duplantier on 6/3/17.
 */

public class User {

    FirebaseDatabase database;
    DatabaseReference myRef;


    /*
     * Constructor. Make connection to the realtime database.
     */
    public User() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    public String[] getFriends() {
        return null;
    }
}
