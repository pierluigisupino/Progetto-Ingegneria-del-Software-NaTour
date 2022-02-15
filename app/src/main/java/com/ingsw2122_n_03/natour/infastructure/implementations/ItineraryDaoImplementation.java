package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.annotation.SuppressLint;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.infastructure.implementations.AmplifyImplementations.Analytics;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

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
            jsonObject.put("modifiedsince", iter.getModifiedSince());

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
                           Analytics.recordPositiveEvent("InsertItinerary");
                           controller.onItineraryInsertSuccess(i);
                       }catch(NumberFormatException e) {
                           Analytics.recordNegativeEvent("InsertItinerary", "Something went wrong");
                           controller.onItineraryInsertError();
                       }
                    },

                error -> {
                    Analytics.recordNegativeEvent("InsertItinerary", error.getMessage());
                    controller.onItineraryInsertError();
                }
        );

    }


    @SuppressLint("NewApi")
    @Override
    public ArrayList<Itinerary> getSetUpItineraries() {

        CompletableFuture<ArrayList<Itinerary>> completableFuture = new CompletableFuture<>();

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {
                        completableFuture.complete(parseItineraries(response.getData().asJSONObject().getJSONArray("Result")));
                    } catch (JSONException e) {
                        completableFuture.cancel(true);
                    }

                },

                error -> {

                    if(Objects.requireNonNull(error.getCause()).toString().contains("timeout"))
                        completableFuture.completeExceptionally(new TimeoutException());
                    else
                        completableFuture.cancel(true);

                }

        );

        return completableFuture.join();

    }


    @SuppressLint("NewApi")
    @Override
    public ArrayList<Itinerary> getRecentItineraries() {

        CompletableFuture<ArrayList<Itinerary>> completableFuture = new CompletableFuture<>();

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries")
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {
                        completableFuture.complete(parseItineraries(response.getData().asJSONObject().getJSONArray("Result")));
                    } catch (JSONException e) {
                        completableFuture.completeExceptionally(new Exception());
                    }

                },

                error -> completableFuture.completeExceptionally(new Exception())

        );

        return completableFuture.join();

    }


    @SuppressLint("NewApi")
    @Override
    public ArrayList<Itinerary> getOlderItineraries(int iterId) {

        CompletableFuture<ArrayList<Itinerary>> completableFuture = new CompletableFuture<>();

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
                        completableFuture.complete(parseItineraries(response.getData().asJSONObject().getJSONArray("Result")));
                    } catch (JSONException e) {
                        completableFuture.completeExceptionally(new Exception());
                    }

                },

                error -> completableFuture.completeExceptionally(new Exception())

        );

        return completableFuture.join();

    }


    @SuppressLint("NewApi")
    @Override
    public void putItineraryFromFeedback(Itinerary iter) {

        long newModifiedSince = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("iterid", iter.getIterId());
            jsonObject.put("difficulty", iter.getDifficulty());
            jsonObject.put("hours", iter.getDuration().getHourOfDay());
            jsonObject.put("minutes", iter.getDuration().getMinuteOfHour());
            jsonObject.put("modifiedsince", iter.getModifiedSince());
            jsonObject.put("newmodifiedsince", newModifiedSince);

        } catch (JSONException e) {
            controller.onUpdateItineraryError(false);
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

                        int statusCode = response.getData().asJSONObject().getInt("Code");

                        if(statusCode == 200) {
                            iter.setModifiedSince(newModifiedSince);
                            Analytics.recordPositiveEvent("ItineraryFeedback");
                            controller.onUpdateItinerarySuccess();
                            return;
                        }

                        Analytics.recordNegativeEvent("ItineraryFeedback", String.valueOf(statusCode));
                        controller.onUpdateItineraryError(statusCode == 100);

                    } catch (JSONException e) {
                        Analytics.recordNegativeEvent("ItineraryFeedback", "Something went wrong");
                        controller.onUpdateItineraryError(false);
                    }

                },
                error -> {
                    Analytics.recordNegativeEvent("ItineraryFeedback", error.getMessage());
                    controller.onUpdateItineraryError(false);
                }
        );

    }


    @SuppressLint("NewApi")
    @Override
    public void putItineraryByAdmin(Itinerary iter) {

        long newModifiedSince = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("iterid", iter.getIterId());
            jsonObject.put("name", iter.getName());
            jsonObject.put("iterDescription", iter.getDescription());
            jsonObject.put("difficulty", iter.getDifficulty());
            jsonObject.put("hours", iter.getDuration().getHourOfDay());
            jsonObject.put("minutes", iter.getDuration().getMinuteOfHour());
            jsonObject.put("updateDate", iter.getEditDate());
            jsonObject.put("modifiedsince", iter.getModifiedSince());
            jsonObject.put("newmodifiedsince", newModifiedSince);

        } catch (JSONException e) {
            controller.onUpdateItineraryError(false);
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

                        int statusCode = response.getData().asJSONObject().getInt("Code");

                        if(statusCode == 200) {
                            iter.setModifiedSince(newModifiedSince);
                            controller.onUpdateItinerarySuccess();
                            return;
                        }

                        controller.onUpdateItineraryError(statusCode == 100);

                    } catch (JSONException e) {
                        controller.onUpdateItineraryError(false);
                    }

                },
                error -> controller.onUpdateItineraryError(false)
        );

    }


    @Override
    public void deleteItinerary(int iterId) {

        RestOptions options = RestOptions.builder()
                .addPath("/items/itineraries/"+iterId)
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

                creator.setName(jsonObject.getString("creatorname"));

                Date shareDate = Date.from(Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(jsonObject.getString("sharedate"))));

                Date editDate = null;
                if(!jsonObject.isNull("updatedate"))
                    editDate = Date.from(Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(jsonObject.getString("updatedate"))));

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

                long modifiedSince = jsonObject.getLong("modifiedsince");

                Itinerary iter = new Itinerary(name, difficulty, duration, startPoint, creator, shareDate);

                iter.setIterId(id);
                iter.setWayPoints(iterWaypoints);
                iter.setDescription(description);
                iter.setEditDate(editDate);
                iter.setModifiedSince(modifiedSince);
                
                iters.add(iter);

            }

            return iters;

    }


}
