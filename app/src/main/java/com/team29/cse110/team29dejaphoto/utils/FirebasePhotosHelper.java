package com.team29.cse110.team29dejaphoto.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;
import com.team29.cse110.team29dejaphoto.models.DejaPhoto;
import com.team29.cse110.team29dejaphoto.utils.DejaPhotoLoader;

/**
 * Created by Noah on 5/31/2017.
 */

public class FirebasePhotosHelper {

    private final String TAG = "FirebasePhotosHelper";

    private PhotoLoader photoLoader = new DejaPhotoLoader();

    //Firebase reference for accessing stored media
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    //Firebase reference for getting user information
    private FirebaseDatabase database;
    private DatabaseReference myFirebaseRef;
    private FirebaseUser user;

    //For checking if upload was successful or not
    private UploadTask uploadTask;


    public void upload(DejaPhoto photo)
    {
        //safety check
        if(photo == null) return;

        //Gets current User
        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("Loader", "Uploading: "+ photo.getPhotoUri().getLastPathSegment());

        String photoname = photo.getPhotoUri().getLastPathSegment();

        String shortName = photoname.substring(0,photoname.indexOf("."));

        String userName = user.getEmail().substring(0, user.getEmail().indexOf('@'));

        //Sets reference to current user
        StorageReference userRef = storageRef.child(userName);


        myFirebaseRef.child(userName).child("Photos").child(shortName).setValue(true);
        DatabaseReference userPhotos = myFirebaseRef.child(userName).child("Photos");

        userPhotos.child(shortName).child("Karma").setValue("0");
        userPhotos.child(shortName).child("Released").setValue(false);

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg")
                .setCustomMetadata("Karma", "0").build();


        //Creates new child reference of current user for photo and uploads photo
            StorageReference photoRef = userRef.child(photoname);
            photoRef.updateMetadata(metadata);
            uploadTask = photoRef.putFile(photo.getPhotoUri(), metadata);

    }


    public byte[] downloadFriends()
    {
        //Gets current User
        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        String userName = user.getEmail().substring(0, user.getEmail().indexOf('@'));

        //Sets reference to current user in realtime database
        //StorageReference storageUserRef = storageRef.child(userName);
        DatabaseReference dataFriendsRef = myFirebaseRef.child(userName).child("friends");

        Query friendsQuery = dataFriendsRef;
        friendsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot friend : dataSnapshot.getChildren())
                {
                    Log.d("Friends", "Friends are: " + friend.getKey());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return null;
    }
}
