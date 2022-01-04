package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.app.Activity;
import android.util.Log;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.ItineraryDaoInterface;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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


        Amplify.API.post(
                options,
                response -> {
                    Log.i("RESPONSE", response.getData().asString());
                       try {
                           int i = Integer.parseInt(response.getData().asString());
                           controller.onItineraryInsertSuccess(i);
                       }catch(NumberFormatException e) {
                           controller.onItineraryInsertError();
                       }
                    },

                error -> {controller.onItineraryInsertError(); Log.e("ERROR", error.getMessage());}
        );

    }

    /** CODE REVIEW & ADD SHARE DATE TO GET **/
    public void getItineraries(Activity callingActivity) {

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .build();

        Amplify.API.get(
                options,

                response -> {

                    ArrayList<Itinerary> iters = new ArrayList<>();

                    try {

                        JSONArray result = response.getData().asJSONObject().getJSONArray("Result");

                        for(int i = 0; i < result.length(); ++i) {

                            JSONObject jsonObject = result.getJSONObject(i);

                            int id = jsonObject.getInt("iterid");
                            String name = jsonObject.getString("itername");
                            String description = jsonObject.getString("description");
                            String difficulty = jsonObject.getString("difficulty");
                            int hours = jsonObject.getInt("hours");
                            int minutes = jsonObject.getInt("minutes");
                            String creatorID = jsonObject.getString("creatorid");
                            User creator = new User(creatorID);

                            JSONObject startPointJSON = jsonObject.getJSONObject("startpoint");
                            double x = startPointJSON.getDouble("x");
                            double y = startPointJSON.getDouble("y");
                            WayPoint startPoint = new WayPoint(x, y);

                            ArrayList<WayPoint> iterWaypoints = new ArrayList<>();

                            if(!jsonObject.isNull("waypoints")){

                                JSONArray path = jsonObject.getJSONArray("waypoints");

                                for(int j = 0; j < path.length(); ++j) {
                                    double latitude = Double.parseDouble(path.getJSONObject(j).getString("Latitude"));
                                    double longitude = Double.parseDouble(path.getJSONObject(j).getString("Longitude"));
                                    iterWaypoints.add(new WayPoint(latitude, longitude));
                                }

                            }

                            Itinerary iter = new Itinerary(name, difficulty, hours, minutes, startPoint, creator);

                            iter.setIterId(id);
                            iter.setDescription(description);

                            if(!iterWaypoints.isEmpty())
                                iter.setWayPoints(iterWaypoints);

                            iters.add(iter);

                        }

                    } catch (JSONException e) {
                        controller.onSetUpError(callingActivity);
                    }

                    controller.onSetUpSuccess(iters, callingActivity);

                },

                error -> controller.onSetUpError(callingActivity)

        );

    }

}
