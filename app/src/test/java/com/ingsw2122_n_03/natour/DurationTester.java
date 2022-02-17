package com.ingsw2122_n_03.natour;

import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;

import org.joda.time.LocalTime;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class DurationTester {


    private Itinerary itinerary;

     @Before
     public void setUp() {
         itinerary = new Itinerary(
                 "name", 1, new LocalTime(2, 30), new WayPoint(40.863, 14.2767), new User("tester"), new Date()
         );
     }


    /**
     * INPUT VALIDATION
     * **/

    @Test /* (expected = IllegalArgumentException.class) */
    public void testAverageWithDurationEqualsZero() {
        LocalTime newDuration = new LocalTime(0, 0);
        assertThrows(IllegalArgumentException.class, ()-> itinerary.calculateAverageDuration(newDuration));
    }

    @Test
    public void testAverageWithSameDuration() {
        LocalTime currentDuration = itinerary.getDuration();
        assertEquals(currentDuration, itinerary.calculateAverageDuration(currentDuration));
    }


    /**
     * TEST CASES
     */

    @Test
    public void testAverageWithMinDuration() {
        LocalTime newDuration = new LocalTime(0, 1);
        assertEquals(new LocalTime(1, 15), itinerary.calculateAverageDuration(newDuration));
    }


    @Test
    public void testAverageWithMaxDuration() {
        LocalTime newDuration = new LocalTime(23, 59);
        assertEquals(new LocalTime(13, 14), itinerary.calculateAverageDuration(newDuration));
    }


    @Test
    public void testAverageWithDurationLonger() {
        LocalTime newDuration = new LocalTime(3, 0);
        assertEquals(new LocalTime(2, 45), itinerary.calculateAverageDuration(newDuration));
    }


    @Test
    public void testAverageWithDurationShorter() {
        LocalTime newDuration = new LocalTime(2, 0);
        assertEquals(new LocalTime(2, 15), itinerary.calculateAverageDuration(newDuration));
    }

    @Test
    public void testAverageWithDurationMinusOne() {
        LocalTime newDuration = itinerary.getDuration().minusMinutes(1);
        assertEquals(newDuration, itinerary.calculateAverageDuration(newDuration));
    }

    @Test
    public void testAverageWithDurationPlusOne() {
        LocalTime newDuration = itinerary.getDuration().plusMinutes(1);
        assertEquals(itinerary.getDuration(), itinerary.calculateAverageDuration(newDuration));
    }


}
