package com.ingsw2122_n_03.natour.infastructure.implementations;

import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.UserDaoInterface;
import com.ingsw2122_n_03.natour.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

public final class UserDaoImplementation implements UserDaoInterface {

    private final AuthController authController;

    public UserDaoImplementation(AuthController authController){
        this.authController = authController;
    }

    @Override
    public boolean putUser(User user) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", user.getName());
            jsonObject.put("id", user.getUid());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        AtomicBoolean bool = new AtomicBoolean(false);
        RestOptions options = RestOptions.builder()
                .addPath("/items/user")
                .addBody(jsonObject.toString().getBytes())
                .build();

        Amplify.API.post(options,
                response -> {
                  if(response.getData().asString().equals("Success"))
                      bool.set(true);
                },
                error -> {
                    bool.set(false);
                });

        return bool.get();
    }

}
