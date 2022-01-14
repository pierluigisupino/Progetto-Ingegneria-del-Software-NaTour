package com.ingsw2122_n_03.natour.infastructure.interfaces;

import com.ingsw2122_n_03.natour.model.Itinerary;

import java.util.ArrayList;

public interface ItineraryDaoInterface {

    void postItinerary(Itinerary iter);
    ArrayList<Itinerary> getItineraries();

}
