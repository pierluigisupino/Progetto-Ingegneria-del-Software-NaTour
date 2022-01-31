package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.util.Log;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.MessageDaoInterface;

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
                    Log.i("RESP", response.getData().asString());
                },
                error -> messageController.onRetrieveChatsError()
        );

    }

}
