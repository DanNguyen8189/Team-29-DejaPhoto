package com.team29.cse110.team29dejaphoto;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by David Duplantier and Noah Lovato on 5/13/17.
 */

public class PhotoDatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "PhotoDatabase";
    private static final int DATABASE_VERSION = 1;


    public PhotoDatabaseHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL( "CREATE TABLE PHOTOS (" +
                     "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "DATE_ADDED INTEGER, " +
                     "KARMA INTEGER, " +
                     "RELEASED INTEGER);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void insertPhoto(SQLiteDatabase db, Long dateAdded, int karma, int released) {
        ContentValues photoValues = new ContentValues();
        photoValues.put("DATE_ADDED", dateAdded);
        photoValues.put("KARMA", karma);
        photoValues.put("RELEASED", released);
        db.insert("PHOTOS", null, photoValues);
    }

}
