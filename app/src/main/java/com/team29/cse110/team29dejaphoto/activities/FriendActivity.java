package com.team29.cse110.team29dejaphoto.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.team29.cse110.team29dejaphoto.R;

/**
 * Created by Wis on 5/31/2017.
 */

public class FriendActivity extends AppCompatActivity {

    private final String TAG = "FriendActivity";

    FirebaseDatabase database;
    DatabaseReference myFirebaseRef;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        getSupportActionBar().setTitle("Add Friends");

        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            Toast.makeText(FriendActivity.this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void addFriend(View view) {
        String inputEmail = ((EditText) findViewById(R.id.emailText)).getText().toString();

        final String toAddID = parseEmailToUsername(inputEmail);
        final String myID    = parseEmailToUsername(user.getEmail());

        if(toAddID.length() == 0) {
            Toast.makeText(FriendActivity.this,
                    "Invalid input: Not a valid email address", Toast.LENGTH_SHORT).show();
            return;
        } else if (toAddID.equals(myID)) {
            Toast.makeText(FriendActivity.this,
                    "Invalid input: Cannot add yourself as a friend", Toast.LENGTH_SHORT).show();
            return;
        }

        Query queryUserToAdd = myFirebaseRef.child(toAddID);

        queryUserToAdd.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot == null || snapshot.getValue() == null) {
                    Log.d(TAG, "Attemping to add non-existent user");

                    Toast.makeText(FriendActivity.this,
                            "Invalid input: This user does not exist", Toast.LENGTH_SHORT).show();

                } else {
                    Query queryFriend = myFirebaseRef.child(myID).child("friends").child(toAddID);

                    queryFriend.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            if(snapshot == null || snapshot.getValue() == null) {
                                Query queryRequest = myFirebaseRef.child(myID).child("requests").child(toAddID);

                                queryRequest.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {

                                        if(snapshot == null || snapshot.getValue() == null) {
                                            Log.d(TAG, "No friend request from user being added");

                                            myFirebaseRef.child(toAddID).child("requests").child(myID).setValue(true);

                                            Toast.makeText(FriendActivity.this,
                                                    "Successfully sent friend request to " + toAddID, Toast.LENGTH_SHORT).show();

                                        } else {
                                            Log.d(TAG, "Already have friend request from user being added");

                                            myFirebaseRef.child(myID).child("requests").child(toAddID).removeValue();

                                            myFirebaseRef.child(myID).child("friends").child(toAddID).setValue(true);
                                            myFirebaseRef.child(toAddID).child("friends").child(myID).setValue(true);

                                            Toast.makeText(FriendActivity.this,
                                                    "You are now friends with " + toAddID, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Log.d(TAG, "Attemping to add a user who is already your friend");

                                Toast.makeText(FriendActivity.this,
                                        "Invalid input: Cannot add this friend again", Toast.LENGTH_SHORT).show();
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

    }

    private String parseEmailToUsername(String email) {
        return email.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]+(.[a-zA-Z]{2,})$")
                ? email.substring(0, email.indexOf('@')) : "";
    }
}
