package com.team29.cse110.team29dejaphoto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        getSupportActionBar().setTitle("Friend");

        database = FirebaseDatabase.getInstance();
        myFirebaseRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void addFriend(View view) {
        final String myID = user.getEmail().substring(0, user.getEmail().indexOf('@'));

        String email = ((EditText) findViewById(R.id.emailText)).getText().toString();
        if(email.indexOf('@') == -1) return;
        final String toAddID = email.substring(0, email.indexOf('@'));

        Query queryUserToAdd = myFirebaseRef.child(toAddID);

        queryUserToAdd.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot == null || snapshot.getValue() == null) {
                    Log.d(TAG, "Attemping to add non-existent user");

                } else {
                    Query queryRequest = myFirebaseRef.child(myID).child("requests").child(toAddID);

                    queryRequest.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            if(snapshot == null || snapshot.getValue() == null) {
                                Log.d(TAG, "No friend request from user being added");

                                myFirebaseRef.child(toAddID).child("requests").child(myID).setValue(true);

                            } else {
                                Log.d(TAG, "Already have friend request from user being added");

                                myFirebaseRef.child(myID).child("requests").child(toAddID).removeValue();

                                myFirebaseRef.child(myID).child("friends").child(toAddID).setValue(true);
                                myFirebaseRef.child(toAddID).child("friends").child(myID).setValue(true);
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
}
