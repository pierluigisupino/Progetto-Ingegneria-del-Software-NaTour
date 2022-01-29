package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.annotation.SuppressLint;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.ItineraryDaoInterface;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;

import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            jsonObject.put("hours", iter.getDuration().getHourOfDay());
            jsonObject.put("minutes", iter.getDuration().getMinuteOfHour());
            jsonObject.put("creator", iter.getCreator().getUid());
            jsonObject.put("startPoint", iter.getStartPoint());
            if(!iter.getWayPoints().isEmpty())
                jsonObject.put("waypoints", iter.getWayPoints());
            jsonObject.put("date", iter.getShareDate());

        } catch (JSONException e) {
            controller.onItineraryInsertError();
            return;
        }

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .addBody(jsonObject.toString().getBytes())
                .build();


        Amplify.API.post(
                options,
                response -> {

                       try {
                           int i = Integer.parseInt(response.getData().asString());
                           controller.onItineraryInsertSuccess(i);
                       }catch(NumberFormatException e) {
                           controller.onItineraryInsertError();
                       }
                    },

                error -> controller.onItineraryInsertError()
        );

    }


    @Override
    public void getSetUpItineraries() {

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {
                        ArrayList<Itinerary> iters = parseItineraries(response.getData().asJSONObject().getJSONArray("Result"));
                        controller.onSetUpSuccess(iters);
                    } catch (JSONException e) {
                        controller.onSetUpError(false);
                    }

                },

                error -> controller.onSetUpError(Objects.requireNonNull(error.getCause()).toString().contains("timeout"))

        );

    }


    @Override
    public void getRecentItineraries() {

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {
                        ArrayList<Itinerary> iters = parseItineraries(response.getData().asJSONObject().getJSONArray("Result"));
                        controller.onUpdateItinerariesSuccess(iters);
                    } catch (JSONException e) {
                        controller.onUpdateError();
                    }

                },

                error -> controller.onUpdateError()

        );

    }


    @Override
    public void getOlderItineraries(int iterId) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("iterid", String.valueOf(iterId));

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {
                        ArrayList<Itinerary> iters = parseItineraries(response.getData().asJSONObject().getJSONArray("Result"));
                        controller.onRetrieveItinerarySuccess(iters);
                    } catch (JSONException e) {
                        controller.onUpdateError();
                    }

                },

                error -> controller.onUpdateError()

        );

    }


    @Override
    public void putItineraryFromFeedback(Itinerary iter) {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("iterid", iter.getIterId());
            jsonObject.put("difficulty", iter.getDifficulty());
            jsonObject.put("hours", iter.getDuration().getHourOfDay());
            jsonObject.put("minutes", iter.getDuration().getMinuteOfHour());

        } catch (JSONException e) {
            controller.onItineraryUpdateError();
            return;
        }

        RestOptions options = RestOptions.builder()
                .addPath("/items/feedback")
                .addBody(jsonObject.toString().getBytes())
                .build();

        Amplify.API.put(
                options,
                response ->{

                    try {
                        if(response.getData().asJSONObject().getInt("Code") == 200)
                            controller.onItineraryUpdateSuccess();
                        else
                            controller.onItineraryUpdateError();
                    } catch (JSONException e) {
                        controller.onItineraryUpdateError();
                    }

                },
                error -> controller.onItineraryUpdateError()
        );

    }


    @Override
    public void putItineraryByAdmin(Itinerary iter) {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("iterid", iter.getIterId());
            jsonObject.put("name", iter.getName());
            jsonObject.put("iterDescription", iter.getDescription());
            jsonObject.put("difficulty", iter.getDifficulty());
            jsonObject.put("hours", iter.getDuration().getHourOfDay());
            jsonObject.put("minutes", iter.getDuration().getMinuteOfHour());
            jsonObject.put("updateDate", iter.getEditDate());

        } catch (JSONException e) {
            controller.onItineraryUpdateError();
            return;
        }

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .addBody(jsonObject.toString().getBytes())
                .build();

        Amplify.API.put(
                options,
                response ->{

                    try {
                        if(response.getData().asJSONObject().getInt("Code") == 200)
                            controller.onItineraryUpdateSuccess();
                        else
                            controller.onItineraryUpdateError();
                    } catch (JSONException e) {
                        controller.onItineraryUpdateError();
                    }

                },
                error -> controller.onItineraryUpdateError()
        );

    }


    @Override
    public void deleteItinerary(int iterId) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("iterid", iterId);
        }catch(JSONException e) {
            controller.onDeleteItineraryError();
        }

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .addBody(jsonObject.toString().getBytes())
                .build();

        Amplify.API.delete(
                options,
                response -> {

                    try {
                        if(response.getData().asJSONObject().getInt("Code") == 200)
                            controller.onDeleteItinerarySuccess();
                        else
                            controller.onDeleteItineraryError();
                    } catch (JSONException e) {
                        controller.onDeleteItineraryError();
                    }

                },
                error -> controller.onDeleteItineraryError()

        );

    }


    /**
     * JSON PARSER
     ***/

    @SuppressLint("NewApi")
    private ArrayList<Itinerary> parseItineraries(JSONArray result) throws JSONException {

            ArrayList<Itinerary> iters = new ArrayList<>();

            for(int i = 0; i < result.length(); ++i) {

                JSONObject jsonObject = result.getJSONObject(i);

                int id = jsonObject.getInt("iterid");

                String name = jsonObject.getString("itername");
                
                String description = null;
                if(!jsonObject.isNull("description"))
                    description = jsonObject.getString("description");
                
                int difficulty = jsonObject.getInt("difficulty");

                LocalTime duration = new LocalTime(jsonObject.getInt("hours"),jsonObject.getInt("minutes"));

                String creatorID = jsonObject.getString("creatorid");
                User creator = new User(creatorID);

                DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
                TemporalAccessor accessor;

                accessor = timeFormatter.parse(jsonObject.getString("sharedate"));
                Date shareDate = Date.from(Instant.from(accessor));

                Date editDate = null;
                if(!jsonObject.isNull("updatedate")) {
                    accessor = timeFormatter.parse(jsonObject.getString("updatedate"));
                    editDate = Date.from(Instant.from(accessor));
                }

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

                Itinerary iter = new Itinerary(name, difficulty, duration, startPoint, creator, shareDate);

                iter.setIterId(id);
                iter.setWayPoints(iterWaypoints);
                iter.setDescription(description);
                iter.setEditDate(editDate);
                
                iters.add(iter);

            }

            return iters;

    }


}
