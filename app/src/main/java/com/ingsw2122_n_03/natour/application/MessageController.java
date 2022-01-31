package com.ingsw2122_n_03.natour.application;

import com.ingsw2122_n_03.natour.infastructure.implementations.MessageDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.MessageDaoInterface;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.MessagesFragment;
import com.ingsw2122_n_03.natour.presentation.main.MainActivity;

import java.util.ArrayList;


public class MessageController extends NavigationController{

    private static MessageController instance = null;

    private final MessageDaoInterface messageDaoInterface;

    private User currentUser;
    private final ArrayList<User> chats = new ArrayList<>();

    private MainActivity mainActivity;
    private MessagesFragment messagesFragment;

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

    public void setUpMessages(User currentUser) {
        this.currentUser = currentUser;
        //SHOW LOADING BACKGROUND IN MESSAGE FRAGMENT
        //messageDaoInterface.getChatsByUser(currentUser.getUid());
    }


    /************
     * GET CHATS
     ***********/

    public void updateChats() {
        //SHOW LOADING BACKGROUND IN MESSAGE FRAGMENT
        messageDaoInterface.getChatsByUser(currentUser.getUid());
    }


    public void onRetrieveChatsSuccess(ArrayList<User> chats) {
        this.chats.addAll(chats);
        //UPDATE MESSAGE FRAGMENT
        //DISMISS LOADING BACKGROUND
    }


    public void onRetrieveChatsError() {
        //SHOW ERROR ON MESSAGE FRAGMENT
        //DISMISS LOADING BACKGROUND
    }


    /*********
     * SETTERS
     *********/

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setMessagesFragment(MessagesFragment messagesFragment) {
        this.messagesFragment = messagesFragment;
    }

}
