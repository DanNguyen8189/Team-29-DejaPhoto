package com.team29.cse110.team29dejaphoto.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;

/**
 * Created by David Duplantier on 6/8/17.
 */

public class FirebaseMediator {

    private final String BASE_PATH = "";
    private String fullPath;

    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private DejaPhoto dejaPhoto;

    public FirebaseMediator(String endPath, DejaPhoto dejaPhoto) {
        this.fullPath = BASE_PATH + endPath;
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference(fullPath);
        this.dejaPhoto = dejaPhoto;
    }






}
