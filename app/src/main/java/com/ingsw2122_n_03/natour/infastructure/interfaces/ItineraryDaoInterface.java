package com.ingsw2122_n_03.natour.infastructure.interfaces;

import android.app.Activity;

import com.ingsw2122_n_03.natour.model.Itinerary;

public interface ItineraryDaoInterface {

    void postItinerary(Itinerary iter);
    void getItineraries(Activity callingActivity);

}
