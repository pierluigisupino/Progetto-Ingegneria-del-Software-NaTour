package com.ingsw2122_n_03.natour.infastructure.implementations;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.MessageDaoInterface;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                        messageController.onRetrieveChatsError();
                    }

                },
                error -> messageController.onRetrieveChatsError()
        );

    }


    @Override
    public void getMessagesByChat(String uid1, String uid2) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("user1", uid1);
        queryParams.put("user2", uid2);

        RestOptions options = RestOptions.builder()
                .addPath("/items/message")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                response -> {},
                error -> {}
        );

    }


    @Override
    public void sendMessage(Message message) {

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("body", message.getBody());
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
                response -> {},
                error -> { messageController.onMessageSentError(); }
        );

    }


}
