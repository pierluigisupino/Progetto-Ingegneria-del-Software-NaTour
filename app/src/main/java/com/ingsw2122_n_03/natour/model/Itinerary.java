package com.ingsw2122_n_03.natour.infastructure.entities;

import java.util.ArrayList;


public class Itinerary {

    private String iterId;
    private String name;
    private String description;
    private String difficulty;
    private int hoursDuration;
    private int minutesDuration;
    private WayPoint startPoint;
    private ArrayList<WayPoint> wayPoints;


    /**CONSTRUCTORS
     */

    public Itinerary(String name, String difficulty, int hours, int minutes, WayPoint startPoint) {
        this.name = name;
        this.difficulty = difficulty;
        hoursDuration = hours;
        minutesDuration = minutes;
        this.startPoint = startPoint;
    }

    public Itinerary(String name, String difficulty, int hours, int minutes, String id) {
        this.name = name;
        this.difficulty = difficulty;
        hoursDuration = hours;
        minutesDuration = minutes;
        iterId = id;
    }

    /**GETTERS
       &
    SETTERS
    */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getHoursDuration() {
        return hoursDuration;
    }

    public void setHoursDuration(int hoursDuration) {
        if(hoursDuration < 0 || hoursDuration > 23)
            throw new IllegalArgumentException();
        this.hoursDuration = hoursDuration;
    }

    public int getMinutesDuration() {
        return minutesDuration;
    }

    public void setMinutesDuration(int minutesDuration) {
        if(minutesDuration < 0 || minutesDuration > 59)
            throw  new IllegalArgumentException();
        this.minutesDuration = minutesDuration;
    }

    public WayPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(WayPoint startPoint) {
        this.startPoint = startPoint;
    }

    public ArrayList<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(ArrayList<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }

    public String getIterId() {
        return iterId;
    }

    public void setIterId(String iterId) {
        this.iterId = iterId;
    }

}
