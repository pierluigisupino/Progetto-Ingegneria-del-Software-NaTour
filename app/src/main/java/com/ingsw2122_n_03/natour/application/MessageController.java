package com.ingsw2122_n_03.natour.application;

import com.ingsw2122_n_03.natour.infastructure.implementations.MessageDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.MessageDaoInterface;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.ChatFragment;

import java.util.ArrayList;


public class MessageController extends NavigationController{

    private static MessageController instance = null;

    private final MessageDaoInterface messageDaoInterface;

    private User currentUser;
    private ArrayList<User> chats = new ArrayList<>();

    private ChatFragment chatFragment;

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
        messageDaoInterface.getChatsByUser(currentUser.getUid());
    }


    /************
     * GET CHATS
     ***********/

    public void updateChats() {
        messageDaoInterface.getChatsByUser(currentUser.getUid());
    }


    public void onRetrieveChatsSuccess(ArrayList<User> chats) {
        this.chats = chats;
        chatFragment.updateChats(chats);
    }


    public void onRetrieveChatsError() {
        //SHOW ERROR ON MESSAGE FRAGMENT
    }


    /***************
     * GET MESSAGES
     **************/

    public void retrieveMessages(String user) {
        messageDaoInterface.getMessagesByChat(user, currentUser.getUid());
    }


    public void onRetrieveMessagesSuccess() {


    }


    public void onRetrieveMessagesError() {


    }


    /***************
     * SEND MESSAGE
     **************/

    public void sendMessage() {
        //messageDaoInterface.sendMessage(...);
    }


    public void onMessageSentSuccess() {

    }


    public void onMessageSentError() {

    }


    /*********
     * SETTERS
     *********/

    public void setChatFragment(ChatFragment chatFragment) {
        this.chatFragment = chatFragment;
    }

}
