package com.team29.cse110.team29dejaphoto;

import android.content.Context;
import android.content.pm.InstrumentationInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.internal.runner.junit4.AndroidJUnit4Builder;
import android.support.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import com.team29.cse110.team29dejaphoto.activities.MainActivity;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoDownloader;
import com.team29.cse110.team29dejaphoto.utils.DejaPhotoDownloader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by tyler on 6/2/17.
 */
@RunWith(AndroidJUnit4.class)
public class PhotoDownloaderTest {

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    PhotoDownloader photoDownloader;

    Context context;
    List<DejaPhoto> friendsPhotos;
    List<DejaPhoto> myPhotos;

    @Before
    public void setUp() {
        context = main.getActivity().getApplicationContext();
        photoDownloader = new DejaPhotoDownloader(context);
        friendsPhotos = photoDownloader.downloadFriendsPhotos();
        myPhotos = photoDownloader.downloadMyPhotos();
    }

    @Test
    public void downloadAllPhotos() throws Exception {

        assertFalse(!friendsPhotos.isEmpty());
    }

    @Test
    public void downloadMyPhotos() throws Exception {

        assertTrue(myPhotos.size() != 0);
    }

    @Test
    public void downloadFriendsPhotos() throws Exception {

        assertTrue(friendsPhotos.size() == 0);
    }

}