package com.ingsw2122_n_03.natour.infastructure.interfaces;

import com.ingsw2122_n_03.natour.model.User;

public interface MessageDaoInterface {

    void getChatsByUser(String uid);
    void getMessagesByChat(User user1, User user2);

}
