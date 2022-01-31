package com.ingsw2122_n_03.natour.infastructure.interfaces;

import com.ingsw2122_n_03.natour.model.Message;

public interface MessageDaoInterface {

    void getChatsByUser(String uid);
    void getMessagesByChat(String uid1, String uid2);
    void sendMessage(Message message);

}
