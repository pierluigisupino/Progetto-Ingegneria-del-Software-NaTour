package com.ingsw2122_n_03.natour.presentation.support;

import android.graphics.drawable.Drawable;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class PointOfInterest extends Marker {

    Drawable drawable;

    public PointOfInterest(MapView mapView){
        super(mapView);
    }

    public PointOfInterest(MapView mapView, Drawable drawable) {
        super(mapView);
        this.drawable = drawable;
    }

    public void setDrawable(Drawable drawable){
        this.drawable = drawable;
    }

    public Drawable getDrawable(){
        return this.drawable;
    }


}
