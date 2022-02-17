package com.ingsw2122_n_03.natour.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WayPoint wayPoint = (WayPoint) o;
        return Double.compare(wayPoint.latitude, latitude) == 0 && Double.compare(wayPoint.longitude, longitude) == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "{\"Latitude\": " + "\"" + latitude + "\", \"Longitude\": "  + "\"" + longitude + "\"}";
    }

}
