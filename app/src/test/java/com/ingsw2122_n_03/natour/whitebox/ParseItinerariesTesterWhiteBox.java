package com.ingsw2122_n_03.natour.whitebox;

import static org.junit.Assert.assertEquals;

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

    /*
     *  Parsing of 2 itineraries with all fields set
     *      - A json with 2 itineraries is created
     *      - An arrayList with 2 itineraries equivalent to those of the json is created
     *      - The correct result of parseItineraries will be an arrayList equivalent to the previous arrayList
     */
    @Test
    public void parseItineraryFullFields() throws JSONException {

        String itineraryJson = "[" +
                    "{" +
                        "\"iterid\":5,\"itername\":\"Roma\",\"description\":description,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
                        "\"startpoint\":{\"x\":41.91215825489158,\"y\":12.492356197611912}," +
                        "\"waypoints\":[{\"Latitude\":\"41.90795968255628\",\"Longitude\":\"12.493655406267607\"}]," +
                        "\"creatorid\":\"7bba5c72-7fbe-45ad-996a-686c8685a9b8\",\"sharedate\":\"2022-02-15T00:00:00.000Z\"," +
                        "\"updatedate\":\"2022-02-15T00:00:00.000Z\",\"modifiedsince\":\"1644923584176\",\"creatorname\":\"User1\"" +
                    "}," +
                    "{" +
                        "\"iterid\":5,\"itername\":\"Roma\",\"description\":description,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
                        "\"startpoint\":{\"x\":41.91215825489158,\"y\":12.492356197611912}," +
                        "\"waypoints\":[{\"Latitude\":\"41.90795968255628\",\"Longitude\":\"12.493655406267607\"}]," +
                        "\"creatorid\":\"7bba5c72-7fbe-45ad-996a-686c8685a9b8\",\"sharedate\":\"2022-02-15T00:00:00.000Z\"," +
                        "\"updatedate\":\"2022-02-15T00:00:00.000Z\",\"modifiedsince\":\"1644923584176\",\"creatorname\":\"User1\"" +
                    "}," +
                "]";

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
        itinerary.setEditDate(Date.from(Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse("2022-02-15T00:00:00.000Z"))));

        ArrayList<WayPoint> wayPoints = new ArrayList<>();
        wayPoints.add(new WayPoint(41.90795968255628, 12.493655406267607));
        itinerary.setWayPoints(wayPoints);
        itinerary.setModifiedSince(1644923584176L);
        itinerary.getCreator().setName("User1");

        ArrayList<Itinerary> itineraries = new ArrayList<>();
        itineraries.add(itinerary);
        itineraries.add(itinerary);

        ArrayList<Itinerary> result = parseItinerariesMock.parseItineraries(new JSONArray(itineraryJson));
        assertEquals(itineraries, result);
    }

    /*
     *  Parsing of 2 itineraries whit all the nullable fields set to null
     *      - A json with 2 itineraries is created
     *      - An arrayList with 2 itineraries equivalent to those of the json is created
     *      - The correct result of parseItineraries will be an arrayList equivalent to the previous arrayList
     */
    @Test
    public void parseItineraryWithNullFields() throws JSONException {

        String itineraryJson = "[" +
                    "{" +
                        "\"iterid\":5,\"itername\":\"Roma\",\"description\":null,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
                        "\"startpoint\":{\"x\":41.91215825489158,\"y\":12.492356197611912}," +
                        "\"waypoints\":null," +
                        "\"creatorid\":\"7bba5c72-7fbe-45ad-996a-686c8685a9b8\",\"sharedate\":\"2022-02-15T00:00:00.000Z\"," +
                        "\"updatedate\":null,\"modifiedsince\":\"1644923584176\",\"creatorname\":\"User1\"" +
                    "}," +
                    "{" +
                        "\"iterid\":5,\"itername\":\"Roma\",\"description\":null,\"difficulty\":0,\"hours\":1,\"minutes\":0," +
                        "\"startpoint\":{\"x\":41.91215825489158,\"y\":12.492356197611912}," +
                        "\"waypoints\":null," +
                        "\"creatorid\":\"7bba5c72-7fbe-45ad-996a-686c8685a9b8\",\"sharedate\":\"2022-02-15T00:00:00.000Z\"," +
                        "\"updatedate\":null,\"modifiedsince\":\"1644923584176\",\"creatorname\":\"User1\"" +
                    "}," +
                "]";

        Itinerary itinerary = new Itinerary(
                "Roma",
                0,
                new LocalTime(1, 0),
                new WayPoint(41.91215825489158, 12.492356197611912),
                new User("7bba5c72-7fbe-45ad-996a-686c8685a9b8"),
                Date.from(Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse("2022-02-15T00:00:00.000Z")))
        );

        itinerary.setIterId(5);
        itinerary.setModifiedSince(1644923584176L);
        itinerary.getCreator().setName("User1");

        ArrayList<Itinerary> itineraries = new ArrayList<>();
        itineraries.add(itinerary);
        itineraries.add(itinerary);

        ArrayList<Itinerary> result = parseItinerariesMock.parseItineraries(new JSONArray(itineraryJson));
        assertEquals(itineraries, result);
    }

}
