package com.ingsw2122_n_03.natour.infastructure.interfaces;

import com.ingsw2122_n_03.natour.model.User;

public interface UserDaoInterface {

    String getCurrentUserId();
    void setUserName(User user);
    boolean isCurrentUserAdmin() throws InterruptedException;

}
