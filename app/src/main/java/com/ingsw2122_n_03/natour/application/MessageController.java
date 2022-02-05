package com.ingsw2122_n_03.natour.application;

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


    public void retrieveMessages(User endUser) {
        goToActivity(mainActivity, MessagesActivity.class, chats.get(endUser), currentUser, endUser);
    }


    public void onMessageReceived(Message message) {

        if(!chats.containsKey(message.getSender())) {
            chats.put(message.getSender(), new ArrayList<>());
            chatFragment.updateChats(chats);
        }

        Objects.requireNonNull(chats.get(message.getSender())).add(message);

        if(!messageActivity.isDestroyed() && messageActivity.getCurrentSession().equals(message.getSender().getUid()))
            messageActivity.updateChat(message);

    }


    /***************
     * SEND MESSAGE
     **************/

    public void sendMessage(Message message) {
        sentMessage = message;
        webSocket.sendMessage(message);
    }


    public void onMessageSentSuccess() {

        messageActivity.updateChat(sentMessage);
        if(!chats.containsKey(sentMessage.getReceiver()))
            chats.put(sentMessage.getReceiver(), new ArrayList<>());

        Objects.requireNonNull(chats.get(sentMessage.getReceiver())).add(sentMessage);
        chatFragment.updateChats(chats);

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
