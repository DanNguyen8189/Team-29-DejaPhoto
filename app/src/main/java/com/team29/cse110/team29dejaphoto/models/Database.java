package com.team29.cse110.team29dejaphoto.models;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;

import java.util.ArrayList;

/**
 * Created by David Duplantier on 6/3/17.
 */

public class Database {

    private final String TAG = "Database";

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    Query friendsList;
    ArrayList<String> sharedPhotosID = new ArrayList<>();


    /*
     * Constructor. Get reference to the Firebase realtime database.
     */
    public Database() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }



    public String[] getPhotoList() {

        Log.d(TAG, "getPhotosList()");

        String[] friendList = getFriends();
        Query photosList = myRef.child(user.getEmail().substring(0, user.getEmail().indexOf('@')))
                .child("friends");
        //sharedPhotosID.add(myRef.child(getName()).child("friends"));

        photosList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    Query friend = myRef.child(d.getKey()).child("Photos");

                    friend.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot d : dataSnapshot.getChildren()) {
                                if(!((Boolean) d.child("Released").getValue())) {
                                    sharedPhotosID.add(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                         }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        String[] s = new String[sharedPhotosID.size()];
        return sharedPhotosID.toArray(s);
    }

    public String[] getFriends() {

        Log.d(TAG, "getFriends()");



        final ArrayList<DejaPhoto> friendsDejaPhotos = new ArrayList<>();

        final ArrayList<String> friends = new ArrayList<>();

        friendsList = myRef.child(user.getEmail().substring(0, user.getEmail().indexOf('@')))
                           .child("friends");

        friendsList.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    friends.add((String) d.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "DatabaseError occurred: Cannot retrieve data");
            }
        });

        String[] returnArray = new String[friends.size()];
        return friends.toArray(returnArray);
    }

    public String getName() {

        Log.d(TAG, "getName()");

        return user.getEmail().substring(0,user.getEmail().indexOf('@'));
    }

}
