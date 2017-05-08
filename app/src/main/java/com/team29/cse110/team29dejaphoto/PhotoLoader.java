package com.team29.cse110.team29dejaphoto;

import android.content.Context;

/**
 * Created by David Duplantier on 5/8/17.
 */

public interface PhotoLoader {

    public DejaPhoto[] getPhotosAsArray(Context context);

    public DejaPhoto[] getNewPhotosAsArray(Context context);

}
