package com.ingsw2122_n_03.natour.infastructure.implementations;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.UserDaoInterface;
import com.ingsw2122_n_03.natour.model.Admin;
import com.ingsw2122_n_03.natour.model.User;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


public final class UserDaoImplementation implements UserDaoInterface {

    private final IterController controller;


    public UserDaoImplementation(IterController controller) { this.controller = controller; }


    public void getCurrentUser() {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("uid", Amplify.Auth.getCurrentUser().getUserId());

        RestOptions options = RestOptions.builder()
                .addPath("/items/admin")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                result -> {

                    try {
                        if(result.getData().asJSONObject().getBoolean("isAdmin"))
                            controller.setUp(new Admin(Amplify.Auth.getCurrentUser().getUserId()));
                        else
                            controller.setUp(new User(Amplify.Auth.getCurrentUser().getUserId()));
                    } catch (JSONException e) {
                        controller.onSetUpError(false);
                    }

                },

                error -> controller.onSetUpError(false)

        );

    }


    @Override
    public void setUserName(User user) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("uid", user.getUid());

        RestOptions options = RestOptions.builder()
                .addPath("/items/user")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                response -> {

                    if(response.getData().asString().contains("UserNotFoundException")){
                        user.setName("Unknown");
                        controller.onRetrieveUserSuccess();
                        return;
                    }

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
