package com.team29.cse110.team29dejaphoto;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by Noah on 5/31/2017.
 */

public class FirebasePhotosHelper {

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


    public void upload(Context context)
    {
        //Loads photos into an array to be uploaded
        DejaPhoto[] photos = photoLoader.getPhotosAsArray(context);

        Log.d("Loader", "Array size: "+ photos.length);

        //Gets current User
        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Log.d("Loader", "Current User Email: "+ user.getEmail());


        //Sets reference to current user
        StorageReference userRef = storageRef.child(
                user.getEmail().substring(0, user.getEmail().indexOf('@')));

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();

        //iterates through all photos in storage and uploads them to Firebase
        for(int i = 0; i < photos.length; i++) {
            DejaPhoto testPhoto = photos[i];

            //Creates new child reference of current user for photo to be uploaded into
            StorageReference photoRef = userRef.child(testPhoto.getPhotoUri().getLastPathSegment());
            uploadTask = photoRef.putFile(testPhoto.getPhotoUri(), metadata);
        }


    }
}
