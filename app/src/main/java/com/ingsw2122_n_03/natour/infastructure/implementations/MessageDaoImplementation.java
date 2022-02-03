package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.annotation.SuppressLint;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.MessageDaoInterface;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MessageDaoImplementation implements MessageDaoInterface {

    private final MessageController messageController;

    public MessageDaoImplementation(MessageController controller) {
        messageController = controller;
    }


    @Override
    public void getChatsByUser(String uid) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("userid", uid);

        RestOptions options = RestOptions.builder()
                .addPath("/items/chats")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {

                        ArrayList<User> chats = new ArrayList<>();
                        JSONArray jsonArray = response.getData().asJSONObject().getJSONArray("Result");
                        for(int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            User user = new User(jsonObject.getString("userid"));
                            user.setName(jsonObject.getString("name"));
                            chats.add(user);
                        }
                        messageController.onRetrieveChatsSuccess(chats);

                    } catch (JSONException e) {
                        messageController.onRetrieveChatsError(false);
                    }

                },
                error -> messageController.onRetrieveChatsError(Objects.requireNonNull(error.getCause()).toString().contains("timeout"))
        );

    }


    @SuppressLint("NewApi")
    @Override
    public void getMessagesByChat(User user1, User user2) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("user1", user1.getUid());
        queryParams.put("user2", user2.getUid());

        RestOptions options = RestOptions.builder()
                .addPath("/items/messages")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {

                        JSONArray jsonArray = response.getData().asJSONObject().getJSONArray("Result");
                        ArrayList<Message> messages = new ArrayList<>();

                        for(int i = 0; i < jsonArray.length(); ++i) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String body = jsonObject.getString("body");

                            LocalDate sendDate = LocalDate.from(DateTimeFormatter.ISO_DATE_TIME.parse(jsonObject.getString("senddate")));
                            LocalTime sendTime = LocalTime.parse(jsonObject.getString("sendtime").substring(0,5), DateTimeFormatter.ofPattern("HH:mm"));

                            if(jsonObject.getString("sender").equals(user1.getUid()))
                                messages.add(new Message(body, sendDate, sendTime, user1, user2));
                            else
                                messages.add(new Message(body, sendDate, sendTime, user2, user1));

                        }

                        messageController.onRetrieveMessagesSuccess(messages);

                    } catch (JSONException e) {
                        messageController.onRetrieveMessagesError();
                    }

                },
                error -> messageController.onRetrieveMessagesError()
        );

    }


    @Override
    public void sendMessage(Message message) {

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("text", message.getBody());
            jsonObject.put("sender", message.getSender().getUid());
            jsonObject.put("receiver", message.getReceiver().getUid());
            jsonObject.put("sendDate", message.getSendDate());
            jsonObject.put("sendTime", message.getSendTime());
        }catch(JSONException e) {
            messageController.onMessageSentError();
        }

        RestOptions options = RestOptions.builder()
                .addPath("/items/message")
                .addBody(jsonObject.toString().getBytes())
                .build();

        Amplify.API.post(
                options,
                response -> {

                    try {
                        if(response.getData().asJSONObject().getInt("Code") == 200)
                            messageController.onMessageSentSuccess();
                        else
                            messageController.onMessageSentError();
                    } catch (JSONException e) {
                        messageController.onMessageSentError();
                    }

                },
                error -> messageController.onMessageSentError()
        );

    }


}
