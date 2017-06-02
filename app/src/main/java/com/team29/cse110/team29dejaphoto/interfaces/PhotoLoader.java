package com.team29.cse110.team29dejaphoto.interfaces;

import android.content.Context;

import com.team29.cse110.team29dejaphoto.models.DejaPhoto;

/**
 * Created by David Duplantier on 5/8/17.
 */

public interface PhotoLoader {

    public DejaPhoto[] getPhotosAsArray(Context context);

    public DejaPhoto[] getNewPhotosAsArray(Context context);

}
