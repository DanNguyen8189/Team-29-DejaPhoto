package com.team29.cse110.team29dejaphoto;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.test.rule.ActivityTestRule;

import com.team29.cse110.team29dejaphoto.activities.MainActivity;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoDownloader;
import com.team29.cse110.team29dejaphoto.utils.BitmapUtil;
import com.team29.cse110.team29dejaphoto.utils.DejaPhotoDownloader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by tyler on 6/9/17.
 */
public class BitmapUtilTest {

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<MainActivity>(MainActivity.class);

    Bitmap smallImage;
    Bitmap largeImage;
    Bitmap smallVertical;
    Bitmap smallHorizontal;

    Bitmap resizedPhoto;



    BitmapUtil bitmapUtil = new BitmapUtil();
    PhotoDownloader photoDownloader = new DejaPhotoDownloader(main.getActivity().getApplicationContext());

    List<DejaPhoto> photos = photoDownloader.downloadAllPhotos();

    @Before
    public void setUp() {

        smallImage = Bitmap.createBitmap(null,0,0,200,200);
        largeImage = Bitmap.createBitmap(null,0,0,2000,2000);
        smallHorizontal = Bitmap.createBitmap(null,0,0,200,800);
        smallVertical = Bitmap.createBitmap(null,0,0,800,200);

    }

    @Test
    public void resizePhoto() throws Exception {

        resizedPhoto = bitmapUtil.resizePhoto(smallImage);
    }

    @Test
    public void bitmapToByteArray() throws Exception {

    }

    @Test
    public void backgroundImage() throws Exception {

    }

}