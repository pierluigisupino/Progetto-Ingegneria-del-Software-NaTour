package com.ingsw2122_n_03.natour.presentation.support;

import android.graphics.drawable.Drawable;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class PointOfInterest extends Marker {

    private byte[] bytes;

    public PointOfInterest(MapView mapView, byte[] bytes){
        super(mapView);
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

}
