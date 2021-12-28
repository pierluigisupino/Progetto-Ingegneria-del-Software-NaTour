package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.util.Log;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.ItineraryDaoInterface;
import com.ingsw2122_n_03.natour.model.Itinerary;

import org.json.JSONException;
import org.json.JSONObject;

public final class ItineraryDaoImplementation implements ItineraryDaoInterface {

    private final IterController controller;

    public ItineraryDaoImplementation(IterController controller) {
        this.controller = controller;
    }

    @Override
    public void postItinerary(Itinerary iter) {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("name", iter.getName());
            jsonObject.put("iterDescription", iter.getDescription());
            jsonObject.put("difficulty", iter.getDifficulty());
            jsonObject.put("hours", iter.getHoursDuration());
            jsonObject.put("minutes", iter.getMinutesDuration());
            jsonObject.put("creator", iter.getCreator().getUid());
            jsonObject.put("startPoint", iter.getStartPoint());
            jsonObject.put("waypoints", iter.getWayPoints());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .addBody(jsonObject.toString().getBytes())
                .build();

        Amplify.API.post(options,
                response -> Log.i("IterID", response.getData().asString()),//controller.onItineraryInsertSuccess(1),
                error -> controller.onItineraryInsertError(error.getMessage())
        );

    }

}
