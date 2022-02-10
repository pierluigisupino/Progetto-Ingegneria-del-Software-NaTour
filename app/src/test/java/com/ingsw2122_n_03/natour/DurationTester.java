package com.ingsw2122_n_03.natour;

import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;

import org.joda.time.LocalTime;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;


public class DurationTester {

    private final Itinerary itinerary = new Itinerary(
            "name", 1, new LocalTime(2, 30), new WayPoint(40.863, 14.2767), new User("tester"), new Date()
    );


    @Test (expected = IllegalArgumentException.class)
    public void testWithNewDurationEqualsZero() {
        LocalTime newDuration = new LocalTime(0, 0);
        itinerary.calculateAverageDuration(newDuration);
    }

    @Test
    public void testWithNewDurationGreater() {
        LocalTime newDuration = new LocalTime(3, 0);
        assertEquals(itinerary.calculateAverageDuration(newDuration), new LocalTime(2, 45));
    }

    @Test
    public void testWithNewDurationSmaller() {
        LocalTime newDuration = new LocalTime(2, 0);
        assertEquals(itinerary.calculateAverageDuration(newDuration), new LocalTime(2, 15));
    }

}
