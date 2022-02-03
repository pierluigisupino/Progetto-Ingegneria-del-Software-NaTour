package com.ingsw2122_n_03.natour.application;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.infastructure.implementations.MessageDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.MessageDaoInterface;
import com.ingsw2122_n_03.natour.infastructure.webSocket.WebSocketSingleton;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;
import com.ingsw2122_n_03.natour.presentation.chat.ChatFragment;
import com.ingsw2122_n_03.natour.presentation.chat.MessagesActivity;
import com.ingsw2122_n_03.natour.presentation.main.MainActivity;

import java.util.ArrayList;


public class MessageController extends NavigationController{

    private static MessageController instance = null;

    private MainActivity mainActivity;
    private MessagesActivity messageActivity;

    private final MessageDaoInterface messageDaoInterface;
    private final WebSocketSingleton webSocket;

    private User currentUser;
    private User endUser;
    private ArrayList<User> chats = new ArrayList<>();

    private ChatFragment chatFragment;

    private MessageController(){
        messageDaoInterface = new MessageDaoImplementation(this);
        webSocket = WebSocketSingleton.getInstance();
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
        chatFragment.updateChats(chats); //TODO possibile crash
        if(chatFragment.isVisible())
            mainActivity.onSuccess(mainActivity.getString(R.string.messages_updated));
    }


    public void onRetrieveChatsError(boolean isResolvableError) {
        if(isResolvableError) {
            chatFragment.onError(); //TODO possibile crash
            if(chatFragment.isVisible())
                mainActivity.onFail(mainActivity.getString(R.string.generic_error)); //INUTILE?
        }else {
            // AVRO ERROR ACTIVITY DUPLICATE OPPURE CRASHERA?
            goToActivityAndFinish(mainActivity, ErrorActivity.class);
        }
    }


    /***************
     * GET MESSAGES
     **************/

    public void retrieveMessages(User endUser) {
        this.endUser = endUser;
        messageDaoInterface.getMessagesByChat(currentUser, endUser);
    }


    public void onRetrieveMessagesSuccess(ArrayList<Message> messages) {
        goToActivity(mainActivity, MessagesActivity.class, messages, currentUser, endUser);
        //SHOW MESSAGE ACTIVITY TO VIEW MESSAGES WITH END USER
    }


    public void onRetrieveMessagesError() {
        //SHOW ERROR ON CHAT FRAGMENT (EVEN IN ITINERARY DETAIL?)
    }


    public void onMessageReceived(Message message) {
        messageActivity.updateChat(message);
        //CHAT IS VISIBLE? ADD MESSAGE
        //ADD SENDER USER TO CHAT FRAGMENT IF NOT PRESENT
    }


    /***************
     * SEND MESSAGE
     **************/

    public void sendMessage(Message message) {
        webSocket.sendMessage(message);
    }


    public void onMessageSentSuccess() {
        //SHOW MESSAGE IN MESSAGE ACTIVITY
    }


    public void onMessageSentError() {
        //SHOW ERROR IN MESSAGE ACTIVITY
    }


    /*********
     * SETTERS
     *********/

    public void setChatFragment(ChatFragment chatFragment) {
        this.chatFragment = chatFragment;
    }

    public void setMessageActivity(MessagesActivity messageActivity) { this.messageActivity = messageActivity; }

    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }


}
