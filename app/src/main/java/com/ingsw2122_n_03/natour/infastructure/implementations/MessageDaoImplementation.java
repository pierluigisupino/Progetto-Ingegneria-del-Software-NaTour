package com.ingsw2122_n_03.natour.infastructure.implementations;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.MessageDaoInterface;
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

}
