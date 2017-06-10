package com.team29.cse110.team29dejaphoto;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.team29.cse110.team29dejaphoto.activities.MainActivity;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoLoader;
import com.team29.cse110.team29dejaphoto.utils.DejaPhotoLoader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by tyler on 6/2/17.
 */
@RunWith(AndroidJUnit4.class)
public class PhotoLoaderTest {

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);


    PhotoLoader photoLoader;
    DejaPhoto[] array;
    DejaPhoto[] newPhotos;

    @Before
    public void setUp() {
      photoLoader = new DejaPhotoLoader();

    }


    /**
     * This tests that photos are returned from the default storage on the device
     *
     * @throws Exception
     */
    @Test
    public void getPhotosAsArray() throws Exception {

        array = photoLoader.getPhotosAsArray(main.getActivity().getApplicationContext());
        assertTrue(array.length != 0);// Photos are loaded from device
    }


    /**
     * This tests that new Photos, with the camera, are returned from the device
     *
     * @throws Exception
     */
    @Test
    public void getNewPhotosAsArray() throws Exception {


        newPhotos = photoLoader.getNewPhotosAsArray(main.getActivity().getApplicationContext());
        assertTrue(newPhotos.length == 0);// There are no new photos
    }

}