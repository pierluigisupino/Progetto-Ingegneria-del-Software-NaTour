package com.ingsw2122_n_03.natour;

import com.ingsw2122_n_03.natour.mock.ParseItineriesMock;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;

import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ParseItinerariesTester {

    ParseItineriesMock parseItineriesMock;

    @Before
    public void setUp(){
        parseItineriesMock = new ParseItineriesMock();
    }

    @Test
    public void parseItineraryWithEmptyJSONArray() throws JSONException {
        ArrayList<Itinerary> result = parseItineriesMock.parseItineraries(new JSONArray());
        assertTrue(result.isEmpty());
    }

    @Test
    public void parseItineraryWithWrongItineraryJSON() throws JSONException{

        String wrongItineraryJson = "[{\"itername\":\"Roma\",\"description\":null,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
                "\"startpoint\":{\"x\":41.91215825489158,\"y\":12.492356197611912}," +
                "\"waypoints\":[{\"Latitude\":\"41.90795968255628\",\"Longitude\":\"12.493655406267607\"}]," +
                "\"creatorid\":\"7bba5c72-7fbe-45ad-996a-686c8685a9b8\",\"sharedate\":\"2022-02-15T00:00:00.000Z\"," +
                "\"updatedate\":null,\"modifiedsince\":\"1644923584176\",\"creatorname\":\"User1\"}]";

        JSONArray jsonArray = new JSONArray(wrongItineraryJson);
        assertThrows(JSONException.class, () -> parseItineriesMock.parseItineraries(jsonArray));
    }

    @Test
    public void parseItineraryWithCorrectItineraryJSON() throws JSONException {

        String correctItineraryJson = "[{\"iterid\":5,\"itername\":\"Roma\",\"description\":null,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
                "\"startpoint\":{\"x\":41.91215825489158,\"y\":12.492356197611912}," +
                "\"waypoints\":[{\"Latitude\":\"41.90795968255628\",\"Longitude\":\"12.493655406267607\"}]," +
                "\"creatorid\":\"7bba5c72-7fbe-45ad-996a-686c8685a9b8\",\"sharedate\":\"2022-02-15T00:00:00.000Z\"," +
                "\"updatedate\":null,\"modifiedsince\":\"1644923584176\",\"creatorname\":\"User1\"}]";

        Itinerary itinerary = new Itinerary(
                "Roma",
                0,
                new LocalTime(1, 0),
                new WayPoint(41.91215825489158, 12.492356197611912),
                new User("7bba5c72-7fbe-45ad-996a-686c8685a9b8"),
                Date.from(Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse("2022-02-15T00:00:00.000Z")))
        );

        itinerary.setIterId(5);
        ArrayList<WayPoint> wayPoints = new ArrayList<>();
        wayPoints.add(new WayPoint(41.90795968255628,12.493655406267607));
        itinerary.setWayPoints(wayPoints);
        itinerary.setModifiedSince(1644923584176L);
        itinerary.getCreator().setName("User1");

        ArrayList<Itinerary> itineraries = new ArrayList<>();
        itineraries.add(itinerary);

        JSONArray jsonArray = new JSONArray(correctItineraryJson);
        ArrayList<Itinerary> result =  parseItineriesMock.parseItineraries(jsonArray);

        assertEquals(itineraries, result);
    }

    @Test
    public void parseItineraryWithEmptyItineraryJSON(){
        String emptyItineraryJson = "[{}]";
        assertThrows(JSONException.class, () -> parseItineriesMock.parseItineraries(new JSONArray(emptyItineraryJson)));
    }

}
