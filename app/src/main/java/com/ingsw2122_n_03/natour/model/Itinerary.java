package com.ingsw2122_n_03.natour.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Itinerary implements Serializable {

    private        String          name;
    private        String          description;
    private        String          difficulty;
    private        int             hoursDuration;
    private        int             minutesDuration;
    private final  WayPoint        startPoint;
    private        List<WayPoint>  wayPoints = new ArrayList<>();
    private        User            creator;
    private final  Date            shareDate;
    private        Date            editDate;
    private        int             iterId;


    /**CONSTRUCTOR
     */

    public Itinerary(String name, String difficulty, int hours, int minutes, WayPoint startPoint, User creator, Date shareDate) {
        this.name = name;
        this.difficulty = difficulty;
        hoursDuration = hours;
        minutesDuration = minutes;
        this.startPoint = startPoint;
        this.creator = creator;
        this.shareDate = shareDate;
    }

    /**GETTERS
       &
    SETTERS
    */

    public String getName() {
        return name;
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
        if(hoursDuration < 0)
            throw  new IllegalArgumentException();
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


    public List<WayPoint> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(ArrayList<WayPoint> wayPoints) {
        this.wayPoints = wayPoints;
    }


    public int getIterId() {
        return iterId;
    }

    public void setIterId(int iterId) {
        this.iterId = iterId;
    }


    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }


    public Date getEditDate() { return editDate; }

    public void setEditDate(Date editDate) { this.editDate = editDate; }


    public Date getShareDate() { return shareDate; }


}
