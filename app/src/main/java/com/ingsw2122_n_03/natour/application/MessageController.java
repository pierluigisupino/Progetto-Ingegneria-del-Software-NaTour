package com.ingsw2122_n_03.natour.application;

import android.annotation.SuppressLint;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.infastructure.implementations.MessageDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.MessageDaoInterface;
import com.ingsw2122_n_03.natour.infastructure.webSocket.WebSocketSingleton;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.chat.ChatFragment;
import com.ingsw2122_n_03.natour.presentation.chat.MessagesActivity;
import com.ingsw2122_n_03.natour.presentation.main.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class MessageController extends NavigationController{

    private static MessageController instance = null;

    private MainActivity mainActivity;
    private MessagesActivity messageActivity;
    private ChatFragment chatFragment;

    private final MessageDaoInterface messageDaoInterface;
    private WebSocketSingleton webSocket;

    private User currentUser;
    private HashMap<User, ArrayList<Message>> chats = new HashMap<>();
    private Message sentMessage;



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
        messageDaoInterface.getChatsByUser(currentUser);
    }


    public void updateChats() {
        messageDaoInterface.getChatsByUser(currentUser);
    }


    public void onRetrieveChatsSuccess(HashMap<User, ArrayList<Message>> chats) {
        webSocket = WebSocketSingleton.getInstance();
        if(!chats.isEmpty()) {
            this.chats = chats;
            chatFragment.updateChats(chats);
        }
    }


    public void onRetrieveChatsError(boolean isResolvableError) {
        if(isResolvableError) {
            chatFragment.onError();
            if(chatFragment.isVisible())
                mainActivity.onFail(mainActivity.getString(R.string.generic_error));
        }
    }


    /***************
     * GET MESSAGES
     **************/


    public void onChatClick(User endUser) {
        goToActivity(mainActivity, MessagesActivity.class, chats.get(endUser), currentUser, endUser);
    }


    public void onMessageReceived(Message message) {

        for(User endUser : chats.keySet()) {
            if(endUser.getUid().equals(message.getSender().getUid())) {
                Objects.requireNonNull(chats.get(endUser)).add(message);
                return;
            }
        }

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        chats.put(message.getSender(), messages);

        if(!messageActivity.isDestroyed() && messageActivity.getCurrentSession().equals(message.getSender().getUid()))
            messageActivity.updateChat(message);

    }


    public void retrieveMessages(User endUser) {

        for(User mUser : chats.keySet()) {
            if(mUser.getUid().equals(endUser.getUid())) {
                goToActivity(mainActivity, MessagesActivity.class, chats.get(mUser), currentUser, mUser);
                return;
            }
        }

        goToActivity(mainActivity, MessagesActivity.class, new ArrayList<>(), currentUser, endUser);

    }


    /***************
     * SEND MESSAGE
     **************/

    public void sendMessage(Message message) {
        sentMessage = message;
        webSocket.sendMessage(message);
    }


    @SuppressLint("NewApi")
    public void onMessageSentSuccess() {

        messageActivity.updateChat(sentMessage);

        for(User endUser : chats.keySet()) {
            if(endUser.getUid().equals(sentMessage.getReceiver().getUid())) {
                Objects.requireNonNull(chats.get(endUser)).add(sentMessage);
                return;
            }
        }

        ArrayList<Message> messages = new ArrayList<>();
        messages.add(sentMessage);
        chats.put(sentMessage.getReceiver(), messages);

    }


    public void onMessageSentError() {
        //SHOW ERROR
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
