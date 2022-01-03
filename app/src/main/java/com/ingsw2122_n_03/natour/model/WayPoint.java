package com.ingsw2122_n_03.natour.model;

import java.io.Serializable;

public class WayPoint implements Serializable {

    private double latitude;
    private double longitude;

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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    @Override
    public String toString() {
        return "{\"Latitude\": " + "\"" + latitude + "\", \"Longitude\": "  + "\"" + longitude + "\"}";
    }

}
