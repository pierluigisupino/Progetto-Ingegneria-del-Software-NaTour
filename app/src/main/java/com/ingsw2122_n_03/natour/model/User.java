package com.ingsw2122_n_03.natour.model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private final String  userId;
    private       String  name;

    /**CONSTRUCTORS
     */

    public User(String uid) {
        userId = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(name, user.name);
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

    public void setName(String name) {
        this.name = name;
    }

}
