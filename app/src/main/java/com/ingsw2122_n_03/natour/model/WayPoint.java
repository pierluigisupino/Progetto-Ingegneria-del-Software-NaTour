package com.ingsw2122_n_03.natour.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class WayPoint implements Serializable {

    private final double latitude;
    private final double longitude;

    /**CONSTRUCTORS
     */

    public WayPoint(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**GETTERS
       &
    SETTERS
    */

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    @NonNull
    @Override
    public String toString() {
        return "{\"Latitude\": " + "\"" + latitude + "\", \"Longitude\": "  + "\"" + longitude + "\"}";
    }

}
