package com.ingsw2122_n_03.natour.whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ingsw2122_n_03.natour.mock.ParseItinerariesMock;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;

import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ParseItinerariesTesterWhiteBox {

    ParseItinerariesMock parseItinerariesMock;

    @Before
    public void setUp(){
        parseItinerariesMock = new ParseItinerariesMock();
    }

    @Test
    public void parseItineraryFullCoverage() throws JSONException {

        String itineraryJson = "[{\"iterid\":5,\"itername\":\"Roma\",\"description\":description,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
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
        itinerary.setDescription("description");

        ArrayList<WayPoint> wayPoints = new ArrayList<>();
        wayPoints.add(new WayPoint(41.90795968255628, 12.493655406267607));
        itinerary.setWayPoints(wayPoints);
        itinerary.setModifiedSince(1644923584176L);
        itinerary.getCreator().setName("User1");

        ArrayList<Itinerary> itineraries = new ArrayList<>();
        itineraries.add(itinerary);

        ArrayList<Itinerary> result = parseItinerariesMock.parseItineraries(new JSONArray(itineraryJson));
        assertEquals(itineraries, result);
    }

    @Test
    public void parseItineraryNoDescription() throws JSONException{

        String itineraryJson = "[{\"iterid\":5,\"itername\":\"Roma\",\"description\":null,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
                "\"startpoint\":{\"x\":41.91215825489158,\"y\":12.492356197611912}," +
                "\"waypoints\":[{\"Latitude\":\"41.90795968255628\",\"Longitude\":\"12.493655406267607\"}]," +
                "\"creatorid\":\"7bba5c72-7fbe-45ad-996a-686c8685a9b8\",\"sharedate\":\"2022-02-15T00:00:00.000Z\"," +
                "\"updatedate\":null,\"modifiedsince\":\"1644923584176\",\"creatorname\":\"User1\"}]";

        ArrayList<Itinerary> result = parseItinerariesMock.parseItineraries(new JSONArray(itineraryJson));
        assertFalse(result.isEmpty());
        assertNull(result.get(0).getDescription());
    }

    @Test
    public void parseItineraryNoUpdateDate() throws JSONException{

        String itineraryJson = "[{\"iterid\":5,\"itername\":\"Roma\",\"description\":description,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
                "\"startpoint\":{\"x\":41.91215825489158,\"y\":12.492356197611912}," +
                "\"waypoints\":[{\"Latitude\":\"41.90795968255628\",\"Longitude\":\"12.493655406267607\"}]," +
                "\"creatorid\":\"7bba5c72-7fbe-45ad-996a-686c8685a9b8\",\"sharedate\":\"2022-02-15T00:00:00.000Z\"," +
                "\"updatedate\":null,\"modifiedsince\":\"1644923584176\",\"creatorname\":\"User1\"}]";

        ArrayList<Itinerary> result = parseItinerariesMock.parseItineraries(new JSONArray(itineraryJson));
        assertFalse(result.isEmpty());
        assertNull(result.get(0).getEditDate());
    }

    @Test
    public void parseItineraryNoWaypoints() throws JSONException{

        String itineraryJson = "[{\"iterid\":5,\"itername\":\"Roma\",\"description\":description,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
                "\"startpoint\":{\"x\":41.91215825489158,\"y\":12.492356197611912}," +
                "\"waypoints\":null," +
                "\"creatorid\":\"7bba5c72-7fbe-45ad-996a-686c8685a9b8\",\"sharedate\":\"2022-02-15T00:00:00.000Z\"," +
                "\"updatedate\":null,\"modifiedsince\":\"1644923584176\",\"creatorname\":\"User1\"}]";

        ArrayList<Itinerary> result = parseItinerariesMock.parseItineraries(new JSONArray(itineraryJson));

        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getWayPoints().isEmpty());
    }

    @Test
    public void parseItineraryWithEmptyJson() throws JSONException{

        String itineraryJson = "[]";

        ArrayList<Itinerary> result =  parseItinerariesMock.parseItineraries(new JSONArray(itineraryJson));
        assertTrue(result.isEmpty());
    }

}
