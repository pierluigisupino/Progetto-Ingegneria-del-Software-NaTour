package com.ingsw2122_n_03.natour.whitebox;

import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;

import org.joda.time.LocalTime;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DurationTesterWhiteBox {

    private Itinerary itinerary;

    @Before
    public void setUp() {
        itinerary = new Itinerary(
                "name", 1, new LocalTime(2, 30), new WayPoint(40.863, 14.2767), new User("tester"), new Date()
        );
    }


    @Test
    public void testStatementOne() {
        LocalTime newDuration = new LocalTime(0, 0);
        assertThrows(IllegalArgumentException.class, ()-> itinerary.calculateAverageDuration(newDuration));
    }


    @Test
    public void testStatementTwo() {
        LocalTime newDuration = new LocalTime(2, 50);
        assertEquals(new LocalTime(2, 40), itinerary.calculateAverageDuration(newDuration));
    }


}
