package com.ingsw2122_n_03.natour.infastructure.interfaces;

import com.ingsw2122_n_03.natour.infastructure.exceptions.RetrieveMessagesException;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public interface MessageDaoInterface {

    HashMap<User, ArrayList<Message>> getChats(User currentUser) throws RetrieveMessagesException;

}
