package com.ingsw2122_n_03.natour.model;

import java.io.Serializable;

public class User implements Serializable {

    private final String  userId;
    private String  name;
    private final boolean isAdmin = true;

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

    public String getName() {
        return name;
    }

    public boolean getIsAdmin() {
        return this.isAdmin;
    }

    public void setName(String name) {
        this.name = name;
    }
}
