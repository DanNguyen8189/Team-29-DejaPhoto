package com.team29.cse110.team29dejaphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.team29.cse110.team29dejaphoto.activities.MainActivity;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.PhotoDownloader;
import com.team29.cse110.team29dejaphoto.utils.BitmapUtil;
import com.team29.cse110.team29dejaphoto.utils.DejaPhotoDownloader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by tyler on 6/9/17.
 */
@RunWith(AndroidJUnit4.class)
public class BitmapUtilTest {

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<MainActivity>(MainActivity.class);

    Bitmap smallImage;
    Bitmap largeImage;
    Bitmap smallVertical;
    Bitmap smallHorizontal;

    Bitmap resizedPhoto;
    byte[] array;

    Context context;

    BitmapUtil bitmapUtil = new BitmapUtil();

    Bitmap icon;
    @Before
    public void setUp() {
        context = main.getActivity().getApplicationContext();
        icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.custom_icon_med);

        icon = Bitmap.createScaledBitmap(icon, 2000, 2000, true);

        smallImage = Bitmap.createBitmap(icon,0,0,200,200);
        largeImage = Bitmap.createBitmap(icon,0,0,2000,2000);
        smallHorizontal = Bitmap.createBitmap(icon,0,0,200,800);
        smallVertical = Bitmap.createBitmap(icon,0,0,800,200);

    }

    @Test
    public void resizePhoto() throws Exception {

        assert(resizedPhoto == bitmapUtil.resizePhoto(smallImage));
    }

    @Test
    public void bitmapToByteArray() throws Exception {

        assert(array == bitmapUtil.bitmapToByteArray(smallImage));
    }

}