package com.ingsw2122_n_03.natour.model;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class NaTourMarker extends Marker {

    private GeoPoint geoPoint;

    public NaTourMarker(MapView mapView) {
        super(mapView);
    }

    public class NaTourGeoPoint extends GeoPoint{

        public NaTourGeoPoint(double aLatitude, double aLongitude) {
            super(aLatitude, aLongitude);
            geoPoint = this;
        }
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
}
