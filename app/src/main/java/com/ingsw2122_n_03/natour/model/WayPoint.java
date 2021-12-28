package com.ingsw2122_n_03.natour.model;

public class WayPoint {

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

    @Override
    public String toString() {
        return "{\"Latitude\": " + "\"" + this.getLatitude() + "\", \"Longitude\": "  + "\"" + this.getLongitude() + "\"}";
    }


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
