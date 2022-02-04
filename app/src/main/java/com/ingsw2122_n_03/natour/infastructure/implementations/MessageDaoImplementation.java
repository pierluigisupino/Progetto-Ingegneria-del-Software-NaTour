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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MessageDaoImplementation implements MessageDaoInterface {

    private final MessageController messageController;

    public MessageDaoImplementation(MessageController controller) {
        messageController = controller;
    }


    @SuppressLint("NewApi")
    @Override
    public void getChatsByUser(User currentUser) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("userid", currentUser.getUid());

        RestOptions options = RestOptions.builder()
                .addPath("/items/chats")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {

                        HashMap<User, ArrayList<Message>> chats = new HashMap<>();
                        JSONArray jsonArray = response.getData().asJSONObject().getJSONArray("Result");

                        for(int i = 0; i < jsonArray.length(); ++i) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            User endUser = new User(jsonObject.getString("userid"));
                            endUser.setName(jsonObject.getString("name"));
                            JSONArray chat = jsonObject.getJSONArray("chat");
                            ArrayList<Message> messages = new ArrayList<>();

                            for(int j = 0; j < chat.length(); ++j) {

                                JSONObject message = chat.getJSONObject(j);
                                String body = message.getString("body");

                                long millis = message.getLong("time");
                                LocalDateTime sendTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());

                                if(message.getString("sender").equals(currentUser.getUid()))
                                    messages.add(new Message(body, sendTime, currentUser, endUser));
                                else
                                    messages.add(new Message(body, sendTime, endUser, currentUser));

                            }

                            chats.put(endUser, messages);

                        }

                        messageController.onRetrieveChatsSuccess(chats);

                    } catch (JSONException e) {
                        messageController.onRetrieveChatsError(false);
                    }

                },
                error -> messageController.onRetrieveChatsError(Objects.requireNonNull(error.getCause()).toString().contains("timeout"))
        );

    }


}
