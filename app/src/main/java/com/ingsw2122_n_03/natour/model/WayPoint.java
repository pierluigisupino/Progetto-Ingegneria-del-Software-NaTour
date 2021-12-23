package com.ingsw2122_n_03.natour.model;

public class WayPoint {

    private double latitude;
    private double longitude;

    /**CONSTRUCTORS
     */

    public WayPoint(double lat, double longitude){
        latitude = lat;
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
}
