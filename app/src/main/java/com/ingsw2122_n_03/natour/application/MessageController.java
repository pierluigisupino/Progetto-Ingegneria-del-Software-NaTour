package com.ingsw2122_n_03.natour.application;

import android.annotation.SuppressLint;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.infastructure.exceptions.RetrieveMessagesException;
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

    private User currentUser;
    private HashMap<User, ArrayList<Message>> chats = new HashMap<>();
    private Message sentMessage;



    private MessageController(){
        messageDaoInterface = new MessageDaoImplementation();
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
        try{
            chats = messageDaoInterface.getChats(currentUser);
            WebSocketSingleton.getInstance();
            chatFragment.updateChats(chats);
        }catch (RetrieveMessagesException e) {
            onRetrieveChatsError();
        }
    }


    public void updateChats() {
        setUpMessages(currentUser);
    }


    public void onRetrieveChatsError() {
        chatFragment.onError();
        if(chatFragment.isVisible())
            mainActivity.onFail(mainActivity.getString(R.string.generic_error));
    }


    /***************
     * GET MESSAGES
     **************/


    public void onChatClick(User endUser) {
        ArrayList<Message> messages = chats.get(endUser);
        assert messages != null;
        messages.get(messages.size()-1).setIsRead(true);
        goToActivity(mainActivity, MessagesActivity.class, chats.get(endUser), currentUser, endUser);
    }


    public void onMessageReceived(Message message) {

        boolean isNewChat = true;

        for (User endUser : chats.keySet()) {
            if (endUser.getUid().equals(message.getSender().getUid())) {
                Objects.requireNonNull(chats.get(endUser)).add(message);
                isNewChat = false;
                break;
            }
        }

        if (isNewChat) {
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(message);
            chats.put(message.getSender(), messages);
        }


        if (messageActivity != null && !messageActivity.isDestroyed() && messageActivity.getCurrentSession().equals(message.getSender().getUid()))
            messageActivity.updateChat(message);
        else
            message.setIsRead(false);


        chatFragment.updateChats(chats);

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
        WebSocketSingleton.getInstance().sendMessage(message);
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
        if(!messageActivity.isDestroyed()){
            messageActivity.onFail(messageActivity.getString(R.string.generic_error));
        }
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
