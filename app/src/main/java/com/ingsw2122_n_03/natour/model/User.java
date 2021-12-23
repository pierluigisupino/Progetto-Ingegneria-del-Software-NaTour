package com.ingsw2122_n_03.natour.model;

public class User {

    /**CONSTRUCTORS
     */

    private String userId;
    private String name;

    /**GETTERS
       &
    SETTERS
    */

    public User(String uid, String name) {
        userId = uid;
        this.name = name;
    }

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
