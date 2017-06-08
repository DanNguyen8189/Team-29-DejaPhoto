package com.team29.cse110.team29dejaphoto.utils;

/**
 * Created by David Duplantier on 6/8/17.
 */

public class FirebaseMediator {

    private final String BASE_PATH = "";

    private String fullPath;

    public FirebaseMediator(String endPath) {
        this.fullPath = BASE_PATH + endPath;
    }
}
