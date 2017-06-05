package com.team29.cse110.team29dejaphoto.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.team29.cse110.team29dejaphoto.interfaces.DejaPhoto;
import com.team29.cse110.team29dejaphoto.interfaces.ReleaseStrategy;
import com.team29.cse110.team29dejaphoto.models.DisplayCycle;
import com.team29.cse110.team29dejaphoto.models.LocalPhoto;
import com.team29.cse110.team29dejaphoto.models.RemotePhoto;
//import com.team29.cse110.team29dejaphoto.models.User;

/**
 * Created by David Duplantier on 6/4/17.
 */

public class ReleaseSharingStrategy implements ReleaseStrategy {

    DisplayCycle displayCycle;
    //User user;

    FirebaseDatabase database;
    DatabaseReference myRef;

    public ReleaseSharingStrategy(DisplayCycle displayCycle) {
        this.displayCycle = displayCycle;
        //this.user = new User();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    @Override
    public int releasePhoto() {

        DejaPhoto dejaPhoto = displayCycle.getCurrentPhoto();
        if ( dejaPhoto instanceof RemotePhoto) {
            return 1;
        }

        String idToRelease = dejaPhoto.getUniqueID();
        int result = notifyFriends(idToRelease);
        return result;

    }

    private int notifyFriends(String idToRelease) {

        //String[] friends = user.getFriends();
        //for ( String friend : friends ) {

        //}
        return 0;
    }

}
