package com.team29.cse110.team29dejaphoto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Locale;

/**
 * Created by RobertChance on 6/3/17.
 */

public class BitmapUtil {

    private Context context;

    private static final int PAINT_SIZE_CONSTANT = 50;  // Constant to derive brush size
    private static final String TAG = "BitmapUtil";

    public BitmapUtil(Context context) {
        this.context = context;
    }

    public BitmapUtil() {
        this.context = null;
    }

    public Bitmap resizePhoto(Bitmap bitmap) {

        return bitmap.getHeight()>= 4*bitmap.getWidth()/3
                ? Bitmap.createScaledBitmap(Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getWidth()*4/3),480,640,true)
                : Bitmap.createScaledBitmap(Bitmap.createBitmap(bitmap,0,0,bitmap.getHeight()*3/4,bitmap.getHeight()),480,640,true);
    }

    public byte[] bitmapToByteArray(Bitmap b) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    /**
     * This method takes a bitmap image and location information, and returns a modified bitmap
     * with location info in the bottom left corner. If no information is available (i.e. location
     * is empty), appropriate text is printed.
     *
     * @param bitmap the background image to be modified
     * @param location location info of the image
     * @return returns image with location info as bitmap
     * @throws Exception ArrayIndexOutOfBounds when no location info
     */
    public Bitmap backgroundImage(Bitmap bitmap, Location location, String personalizedLocation, int karma) throws Exception {

        Log.d(TAG, "Writing address to bitmap");

        String locationTag;

        // Geocoder to get address from remote server
        Geocoder geocoder;
        List<Address> list;

        // Generate new bitmap and paint objects for modification
        Bitmap newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        newBitmap = resizePhoto(newBitmap);

        Log.d("Size", "Height: " + newBitmap.getHeight() + ", Width: " + newBitmap.getWidth());

        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setColor(Color.RED);
        paint.setTextSize(newBitmap.getHeight() / PAINT_SIZE_CONSTANT);

        // check if user has set a custom location for this photo
        if (personalizedLocation != null && personalizedLocation != ""){
            locationTag = personalizedLocation;
        }
        else {

            // get address for location
            try {

                // Geocoder to get address from remote server
                geocoder = new Geocoder(context, Locale.getDefault());
                list = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);

                locationTag = list.get(0).getAddressLine(0);

            }

            // if no valid location
            catch (Exception e) {

                locationTag = "No locationinfo\navailable";
            }
        }

        // Write location info to bitmap and return
        paint.getTextBounds(locationTag, 0, locationTag.length(), rect);
        canvas.drawText(locationTag, 0, newBitmap.getHeight()-newBitmap.getHeight()/5, paint);
        canvas.drawText("Karma: "+karma, newBitmap.getWidth()-2*newBitmap.getWidth()/5, newBitmap.getHeight()-newBitmap.getHeight()/5,paint);

        Log.d(TAG, "Printed location on photo: " + locationTag);

        return newBitmap;
    }

}
