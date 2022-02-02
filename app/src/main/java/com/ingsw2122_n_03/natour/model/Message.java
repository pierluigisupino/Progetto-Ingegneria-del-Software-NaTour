package com.ingsw2122_n_03.natour.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.Serializable;

public class Message implements Serializable {

    private final String     body;
    private final LocalDate  sendDate;
    private final LocalTime  sendTime;
    private final User       sender;
    private final User       receiver;


    /**CONSTRUCTOR
     */

    public Message(String body, LocalDate sendDate, LocalTime sendTime, User sender, User receiver) {
        this.body = body;
        this.sendDate = sendDate;
        this.sendTime = sendTime;
        this.sender = sender;
        this.receiver = receiver;
    }


    /**GETTERS
     &
     SETTERS
     */

    public String getBody() { return body; }

    public LocalDate getSendDate() { return sendDate; }

    public LocalTime getSendTime() { return sendTime; }

    public User getSender() { return sender; }

    public User getReceiver() { return receiver; }

}
