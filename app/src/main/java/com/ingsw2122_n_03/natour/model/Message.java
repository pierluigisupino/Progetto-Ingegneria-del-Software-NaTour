package com.ingsw2122_n_03.natour.model;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Message implements Serializable {

    private final String     body;
    private final long       time;
    private final User       sender;
    private final User       receiver;


    /**CONSTRUCTOR
     */

    public Message(String body, long time, User sender, User receiver) {
        this.body = body;
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
    }


    /**GETTERS
     &
     SETTERS
     */

    public String getBody() { return body; }

    public long getTime() { return time; }

    public User getSender() { return sender; }

    public User getReceiver() { return receiver; }

}
