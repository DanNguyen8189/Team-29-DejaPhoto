package com.team29.cse110.team29dejaphoto.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;
import com.team29.cse110.team29dejaphoto.models.LocalPhoto;
import com.team29.cse110.team29dejaphoto.models.RemotePhoto;

import java.util.ArrayList;

/**
 * Created by Noah on 5/31/2017.
 */

public class FirebasePhotosHelper {

    private final String TAG = "FirebasePhotosHelper";
    final long FIVE_MEGABYTES = 5*1024 * 1024;

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
        // safety check
        if(photo == null) {
            Log.d(TAG, "DejaPhoto is null");
            return;
        }

        //Gets current User
        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoURI = Uri.parse(photo.getUniqueID());

        // Ensure a User is found
        if(user == null) {
            Log.d(TAG, "User authentication failed");
            return;
        }

        Log.d("Loader", "Current User Email: "+ user.getEmail());
        Log.d("Loader", "Uploading: "+ photoURI.getLastPathSegment());

        String photoname = photoURI.getLastPathSegment();
        String shortName = photoname.substring(0,photoname.indexOf("."));
        String userName = user.getEmail().substring(0, user.getEmail().indexOf('@'));

        //Sets reference to current user
        StorageReference userRef = storageRef.child(userName);


        myFirebaseRef.child(userName).child("Photos").child(shortName).setValue(true);
        DatabaseReference userPhotos = myFirebaseRef.child(userName).child("Photos");

        //Uploads the photo's metadata to the realtime database
        userPhotos.child(shortName).child("Karma").setValue(photo.getKarma());
        userPhotos.child(shortName).child("Released").setValue(false);
        userPhotos.child(shortName).child("Latitude").setValue(photo.getLocation().getLatitude());
        userPhotos.child(shortName).child("Longitude").setValue(photo.getLocation().getLongitude());
        userPhotos.child(shortName).child("TimeTaken").setValue(photo.getTime().getTimeInMillis());

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg")
                .setCustomMetadata("Karma", "0").build();


        //Converts photo to a bitmap then resizes before uploading to database
        Bitmap photoBitmap = BitmapFactory.decodeFile(photoURI.getPath());
        BitmapUtil bitmapUtil = new BitmapUtil();

        Bitmap resizedPhoto = bitmapUtil.resizePhoto(photoBitmap);
        byte[] photoByteArray = bitmapUtil.bitmapToByteArray(resizedPhoto);


        //Creates new child reference of current user for photo and uploads photo
        StorageReference photoRef = userRef.child(photoname);
        photoRef.updateMetadata(metadata);
        uploadTask = photoRef.putBytes(photoByteArray, metadata);

    }


    public ArrayList<DejaPhoto> downloadFriends()
    {
        final ArrayList<DejaPhoto> friendsPhotosArray= new ArrayList<DejaPhoto>();

        //Gets current User
        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userName = user.getEmail().substring(0, user.getEmail().indexOf('@'));

        //Sets reference to current user in realtime database
        DatabaseReference dataFriendsRef = myFirebaseRef.child(userName).child("friends");
        Query friendsQuery = dataFriendsRef;

        friendsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for( final DataSnapshot friend : dataSnapshot.getChildren())
                {
                    Log.d("Friends", "Friend is: " + friend.getKey());
                    final StorageReference storageUserRef = storageRef.child(friend.getKey());
                    Query friendsPhotos = myFirebaseRef.child(friend.getKey()).child("Photos");

                    friendsPhotos.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for ( final DataSnapshot friendPhotoRef : dataSnapshot.getChildren() ) {
                                //Gets reference to friend's photos and downloads them to
                                Log.d("Friends", "Downloading "+ friend.getKey() + "'s " +
                                        friendPhotoRef.getKey() +".jpg");
                                StorageReference photoref = storageUserRef.child(friendPhotoRef.getKey() + ".jpg");
                                photoref.getBytes(FIVE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Log.d("Download", "Conversion to bitmap successful");
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
                                        if(bitmap != null) {
                                            Log.d("Download", "Bitmap not null");
                                            RemotePhoto friendPhoto = new RemotePhoto(bitmap, 0, 0, 0);
                                            friendsPhotosArray.add(friendPhoto);
                                            Log.d("Download","Size of returned array List: "+ friendsPhotosArray.size());
                                        }

                                    }
                                });
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
        return friendsPhotosArray;
    }

    public void deleteMyPhotos()
    {
        //Gets current User
        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        String userName = user.getEmail().substring(0, user.getEmail().indexOf('@'));
        final StorageReference storageUserRef = storageRef.child(userName);
        Log.d("Delete", "User is: " + userName);

        //Loops through realtime database to delete user photos
        Query userPhotosRef = myFirebaseRef.child(userName).child("Photos");

        userPhotosRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userPhoto : dataSnapshot.getChildren()) {
                    String photoName = userPhoto.getKey();
                    Log.d("Delete", "Deleting photo: " + photoName);
                    //deletes from storage
                    storageUserRef.child(photoName + ".jpg").delete();
                    userPhoto.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void enableSharing() {
        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        myFirebaseRef.child(user.getEmail().substring(0, user.getEmail().indexOf('@'))).
                      child("SharingEnabled").setValue(true);

    }

    public void disableSharing() {
        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        myFirebaseRef.child(user.getEmail().substring(0, user.getEmail().indexOf('@'))).
                      child("SharingEnabled").setValue(false);
    }
}
