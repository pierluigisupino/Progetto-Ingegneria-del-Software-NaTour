package com.ingsw2122_n_03.natour.infastructure.implementations;

import android.util.Log;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.UserDaoInterface;
import com.ingsw2122_n_03.natour.model.User;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public final class UserDaoImplementation implements UserDaoInterface {

    private final IterController controller;


    public UserDaoImplementation(IterController controller) { this.controller = controller; }


    @Override
    public String getCurrentUserId() {
        return Amplify.Auth.getCurrentUser().getUserId();
    }


    @Override
    public void setCurrentUserName(User user) {

        Amplify.Auth.fetchUserAttributes(
                attributes -> user.setName(attributes.get(2).getValue()),
                error -> {}
        );

    }


    @Override
    public void getNameFromId(User user) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("uid", user.getUid());

        RestOptions options = RestOptions.builder()
                .addPath("items/user")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                response -> {

                    try {
                        String name = response.getData().asJSONObject().getString("Name");
                        user.setName(name);
                        controller.onRetrieveUserSuccess();
                    } catch (JSONException e) {
                        controller.onRetrieveUserError();
                    }

                },
                error -> controller.onRetrieveUserError()
        );

    }

}
