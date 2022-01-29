package com.ingsw2122_n_03.natour.infastructure.interfaces;

import com.ingsw2122_n_03.natour.model.Itinerary;

public interface ItineraryDaoInterface {

    void postItinerary(Itinerary iter);
    void getSetUpItineraries();
    void getRecentItineraries();
    void getOlderItineraries(int iterId);
    void putItineraryFromFeedback(Itinerary iter);
    void putItineraryByAdmin(Itinerary iter);

}
