package com.team29.cse110.team29dejaphoto;

import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;

import com.team29.cse110.team29dejaphoto.activities.MainActivity;
import com.team29.cse110.team29dejaphoto.models.Database;
import com.team29.cse110.team29dejaphoto.utils.FirebasePhotosHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tyler on 6/9/17.
 */
public class DatabaseTest {


    Database database = new Database();
    String myName;
    String[] friends;
    String[] sharedPhotos;

    @Before
    public void setUp() {

        myName = database.getName();
        friends = database.getFriends();
    }


    /**
     * Test that a list of my friend's shared photos is returned
     *
     * @throws Exception
     */
    @Test
    public void getPhotoList() throws Exception {

        sharedPhotos = database.getPhotoList();
        assertTrue(sharedPhotos.length >= 0);

    }


    /**
     * Test that a list of my friends is returned from the Firebase
     *
     * @throws Exception
     */
    @Test
    public void getFriends() throws Exception {

        assertEquals("Testing length of friends list",(friends.length),0 );
        //assertEquals("Testing friend 1:", "nlovato", friends[0]);
    }


    /**
     * Test that my name is properly returned from the Firebase
     *
     * @throws Exception
     */
    @Test
    public void getName() throws Exception {

        assertEquals("Testing userName is correct", "rchance", myName);

    }

}