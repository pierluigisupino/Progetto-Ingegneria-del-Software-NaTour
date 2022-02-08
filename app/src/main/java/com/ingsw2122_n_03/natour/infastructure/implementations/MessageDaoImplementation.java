package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.annotation.SuppressLint;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;


public class MessageDaoImplementation implements MessageDaoInterface {


    @SuppressLint("NewApi")
    @Override
    public HashMap<User, ArrayList<Message>> getChats(User currentUser) {

        CompletableFuture<HashMap<User, ArrayList<Message>>> completableFuture = new CompletableFuture<>();

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

                        JSONArray jsonArray = response.getData().asJSONObject().getJSONArray("Result");
                        completableFuture.complete(parseChats(jsonArray, currentUser));

                    } catch (JSONException e) {
                        completableFuture.cancel(true);
                    }

                },

                error -> {
                    if(Objects.requireNonNull(error.getCause()).toString().contains("timeout"))
                        completableFuture.completeExceptionally(new TimeoutException());
                    else
                        completableFuture.cancel(true);
                }

        );

        return completableFuture.join();

    }


    @SuppressLint("NewApi")
    private HashMap<User, ArrayList<Message>> parseChats(JSONArray jsonArray, User currentUser) throws JSONException {

        HashMap<User, ArrayList<Message>> chats = new HashMap<>();

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

        return chats;

    }



}
