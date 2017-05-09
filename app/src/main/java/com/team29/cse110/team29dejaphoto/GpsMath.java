package com.team29.cse110.team29dejaphoto;

/**
 * Created by Noah on 5/8/2017.
 * To handle all of your GPS distance calculating needs.
 */

public class GpsMath {

    static final long TWO_HOURS = 7200000;  //milliseconds to hours
    static final float ONE_K_FT = 305;   //kilometers to feet
    static final double earthRd = 6371;  //Radius of the earth in Km
    static final double FT_PER_KM = 3280.8399;  //Feet in 1 Km


    //converts degrees to radians
    static double degToRad(double num)
    {
        return num * (Math.PI)/180;
    }

    //calculates distance between 2 GPS coordinates where (x1,y1) is (lat,long) of first location
    //and (x2,y2) is (lat,long) of second location
    //returns distance in Feet.
    static double distanceBetween(double x1, double y1, double x2, double y2)
    {
        double deltaLat = degToRad(x2 - x1);
        double deltaLon = degToRad(y2 - y1);

        double lat1 = degToRad(x1);
        double lat2 = degToRad(x2);

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
                Math.sin(deltaLon/2) * Math.sin(deltaLon/2) * Math.cos(lat1) * Math.cos(lat2);

        double b = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRd * b; //converts from km to feet.
    }


}
