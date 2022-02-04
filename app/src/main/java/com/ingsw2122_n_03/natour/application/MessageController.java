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


public class MessageController extends NavigationController{

    private static MessageController instance = null;

    private MainActivity mainActivity;
    private MessagesActivity messageActivity;

    private final MessageDaoInterface messageDaoInterface;
    private final WebSocketSingleton webSocket;

    private User currentUser;
    private User endUser;
    private ArrayList<User> chats = new ArrayList<>();
    private Message sentMessage;


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
        chatFragment.updateChats(chats);
        if(chatFragment.isVisible())
            mainActivity.onSuccess(mainActivity.getString(R.string.messages_updated));
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
        this.endUser = endUser;
        messageDaoInterface.getMessagesByChat(currentUser, endUser);
    }


    public void onRetrieveMessagesSuccess(ArrayList<Message> messages) {
        goToActivity(mainActivity, MessagesActivity.class, messages, currentUser, endUser);
    }


    public void onRetrieveMessagesError() {
        mainActivity.onFail(mainActivity.getString(R.string.generic_error));
    }


    public void onMessageReceived(Message message) {

        String senderSub = message.getSender().getUid();

        boolean isNewChat = true;
        for (User user : chats) {
            if(user.getUid().equals(senderSub)) {
                isNewChat = false;
                break;
            }
        }

        if(isNewChat) {
            chats.add(message.getSender());
            chatFragment.updateChats(chats);
        }

        if(!messageActivity.isDestroyed())
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

        boolean isNewChat = true;
        for(User user : chats){
            if(user.getUid().equals(endUser.getUid())){
                isNewChat = false;
                break;
            }
        }

        if(isNewChat){
            chats.add(sentMessage.getReceiver());
            chatFragment.updateChats(chats);
        }

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
