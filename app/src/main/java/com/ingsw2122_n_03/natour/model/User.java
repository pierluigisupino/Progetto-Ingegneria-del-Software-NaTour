package com.ingsw2122_n_03.natour.model;

import java.io.Serializable;

public class User implements Serializable {

    private String userId;
    private String name;

    /**CONSTRUCTORS
     */

    public User(String uid) {
        userId = uid;
    }

    /**GETTERS
       &
    SETTERS
    */

    public String getUid() {
        return userId;
    }

    public void setUid(String uid) {
        userId = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
