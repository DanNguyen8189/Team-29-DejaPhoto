package com.team29.cse110.team29dejaphoto;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

/**
 * Created by David Duplantier on 5/13/17.
 */

public class GalleryObserver extends ContentObserver {

    public GalleryObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {

    }

}
