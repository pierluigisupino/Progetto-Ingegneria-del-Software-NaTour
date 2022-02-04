package com.ingsw2122_n_03.natour.model;


import java.io.Serializable;
import java.time.LocalDateTime;


public class Message implements Serializable {

    private final String        body;
    private final LocalDateTime time;
    private final User          sender;
    private final User          receiver;


    /**CONSTRUCTOR
     */

    public Message(String body, LocalDateTime time, User sender, User receiver) {
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

    public LocalDateTime getTime() { return time; }

    public User getSender() { return sender; }

    public User getReceiver() { return receiver; }

}
