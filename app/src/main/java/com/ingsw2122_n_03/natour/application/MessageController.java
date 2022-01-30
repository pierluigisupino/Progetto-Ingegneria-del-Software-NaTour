package com.ingsw2122_n_03.natour.application;

import com.ingsw2122_n_03.natour.infastructure.implementations.MessageDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.MessageDaoInterface;
import com.ingsw2122_n_03.natour.model.User;


public class MessageController extends NavigationController{

    private static MessageController instance = null;

    private final MessageDaoInterface messageDaoInterface;

    private User currentUser;

    private MessageController(){
        messageDaoInterface = new MessageDaoImplementation(this);
    }

    public static MessageController getInstance() {
        if(instance == null){
            instance = new MessageController();
        }
        return instance;
    }


    /*********
     * SET UP
     *********/

    public void getCurrentUserChat(User currentUser) {
        this.currentUser = currentUser;
        messageDaoInterface.getChatsByUser(currentUser.getUid());
    }


    public void onRetrieveChatsSuccess() {

    }


    public void onRetrieveChatsError() {

    }


}
