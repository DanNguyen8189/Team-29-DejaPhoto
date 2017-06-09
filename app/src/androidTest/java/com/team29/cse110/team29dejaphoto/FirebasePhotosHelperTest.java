package com.team29.cse110.team29dejaphoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.team29.cse110.team29dejaphoto.activities.MainActivity;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.models.RemotePhoto;
import com.team29.cse110.team29dejaphoto.utils.FirebasePhotosHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by tyler on 6/6/17.
 */
public class FirebasePhotosHelperTest {

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<MainActivity>(MainActivity.class);

  /*  //Firebase reference for accessing stored media
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    //Firebase reference for getting user information
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myFirebaseRef = database.getReference();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference userRef = storageRef.child(user.getDisplayName()+" Unit Tests");
    */

    public static SharedPreferences sp;
    public final String prefTag = "Test_Prefs";




    FirebasePhotosHelper firebasePhotosHelper;

    DejaPhoto[] gallery = new RemotePhoto[10];

    @Before
    public void setUp(){

//        mainActivity.context
        for(DejaPhoto d: gallery) {
            d = new RemotePhoto(null, 0, 0, 0, Calendar.getInstance().getTimeInMillis(),false, null);
        }
        sp = main.getActivity().getSharedPreferences("Deja_Preference",0);
       // sp = mainActivity.getApplicationContext().getSharedPreferences("Deja_Preferences",0);
        firebasePhotosHelper = new FirebasePhotosHelper(sp);

    }

    @Test
    public void upload() throws Exception {

        for(DejaPhoto d: gallery) {
            firebasePhotosHelper.upload(d);
        }
    }

    @Test
    public void downloadFriends() throws Exception {

    }

    @Test
    public void deleteMyPhotos() throws Exception {

    }

    @Test
    public void enableSharing() throws Exception {

    }

    @Test
    public void disableSharing() throws Exception {

    }

}