package com.ingsw2122_n_03.natour.infastructure.implementations;

import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.infastructure.interfaces.UserDaoInterface;
import com.ingsw2122_n_03.natour.model.User;

public final class UserDaoImplementation implements UserDaoInterface {


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

}
