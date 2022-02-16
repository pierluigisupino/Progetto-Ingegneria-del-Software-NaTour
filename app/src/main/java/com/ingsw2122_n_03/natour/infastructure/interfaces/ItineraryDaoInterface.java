package com.ingsw2122_n_03.natour.infastructure.interfaces;

import com.ingsw2122_n_03.natour.infastructure.exceptions.RetrieveItinerariesException;
import com.ingsw2122_n_03.natour.infastructure.exceptions.SetUpException;
import com.ingsw2122_n_03.natour.model.Itinerary;

import java.util.ArrayList;

public interface ItineraryDaoInterface {

    void postItinerary(Itinerary iter);
    ArrayList<Itinerary> getSetUpItineraries() throws RetrieveItinerariesException, SetUpException;
    ArrayList<Itinerary> getRecentItineraries() throws RetrieveItinerariesException;
    ArrayList<Itinerary> getOlderItineraries(int iterId) throws RetrieveItinerariesException;
    void putItineraryFromFeedback(Itinerary iter);
    void putItineraryByAdmin(Itinerary iter);
    void deleteItinerary(int iterId);

}
