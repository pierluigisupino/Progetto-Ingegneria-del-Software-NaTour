package com.ingsw2122_n_03.natour.model;

import org.joda.time.LocalTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Itinerary implements Serializable {

    private        String          name;
    private        String          description;
    private        String          difficulty;
    private        LocalTime       duration;
    private final  WayPoint        startPoint;
    private        List<WayPoint>  wayPoints = new ArrayList<>();
    private        User            creator;
    private final  Date            shareDate;
    private        Date            editDate;
    private        int             iterId;


    /**CONSTRUCTOR
     */

    public Itinerary(String name, String difficulty, LocalTime duration, WayPoint startPoint, User creator, Date shareDate) {
        this.name = name;
        this.difficulty = difficulty;
        this.duration = duration;
        this.startPoint = startPoint;
        this.creator = creator;
        this.shareDate = shareDate;
    }

    public Itinerary(Itinerary itinerary) {
        this.name = itinerary.getName();
        this.description = itinerary.getDescription();
        this.difficulty = itinerary.getDifficulty();
        this.duration = itinerary.getDuration();
        this.startPoint = itinerary.getStartPoint();
        this.wayPoints = itinerary.getWayPoints();
        this.creator = itinerary.getCreator();
        this.shareDate = itinerary.getShareDate();
        this.editDate = itinerary.getEditDate();
        this.iterId = itinerary.getIterId();
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


    public LocalTime getDuration() { return duration; }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
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


    public int getDifficultyLevel() {

        if(difficulty.contains("(T)"))
            return 0;

        if(difficulty.contains("(E)"))
            return 1;

        if(difficulty.contains("(EE)"))
            return 2;

        return 3;

    }

}
