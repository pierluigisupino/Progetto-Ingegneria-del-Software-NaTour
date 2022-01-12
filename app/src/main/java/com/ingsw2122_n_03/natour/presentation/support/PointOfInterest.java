package com.ingsw2122_n_03.natour.presentation.support;

import android.graphics.drawable.Drawable;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class PointOfInterest extends Marker {

    private byte[] bytes;
    private Drawable drawable;

    public PointOfInterest(MapView mapView, byte[] bytes){
        super(mapView);
        this.bytes = bytes;
    }

    public void setDrawable(Drawable drawable){
        this.drawable = drawable;
    }

    public Drawable getDrawable(){
        return this.drawable;
    }

    public byte[] getBytes() {
        return bytes;
    }

}
