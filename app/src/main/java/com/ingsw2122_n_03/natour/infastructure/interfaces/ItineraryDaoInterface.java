package com.ingsw2122_n_03.natour.infastructure.interfaces;

import com.ingsw2122_n_03.natour.model.Itinerary;

import java.util.ArrayList;

public interface ItineraryDaoInterface {

    void postItinerary(Itinerary iter);
    ArrayList<Itinerary> getSetUpItineraries();
    ArrayList<Itinerary> getRecentItineraries();
    ArrayList<Itinerary> getOlderItineraries(int iterId);
    void putItineraryFromFeedback(Itinerary iter);
    void putItineraryByAdmin(Itinerary iter);
    void deleteItinerary(int iterId);

}
