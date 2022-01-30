package com.ingsw2122_n_03.natour.model;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class Message {

    private String body;
    private LocalDate sendDate;
    private LocalTime sendTime;
    private User sender;
    private User receiver;


    /**CONSTRUCTOR
     */

    public Message(String body, LocalDate sendDate, LocalTime sendTime, User sender, User receiver) {
        this.body = body;
        this.sendDate = sendDate;
        this.sendTime = sendTime;
        this.sender = sender;
        this.receiver = receiver;
    }

}
