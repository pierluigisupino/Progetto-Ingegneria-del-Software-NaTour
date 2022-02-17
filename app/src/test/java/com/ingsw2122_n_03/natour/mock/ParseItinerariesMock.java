package com.ingsw2122_n_03.natour.mock;

import android.annotation.SuppressLint;

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

public class ParseItinerariesMock {


    @SuppressLint("NewApi")
    public ArrayList<Itinerary> parseItineraries(JSONArray result) throws JSONException {

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
