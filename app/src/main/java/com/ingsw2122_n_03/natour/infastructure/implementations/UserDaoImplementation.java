package com.ingsw2122_n_03.natour.infastructure.implementations;

import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.infastructure.interfaces.UserDaoInterface;

public final class UserDaoImplementation implements UserDaoInterface {


    @Override
    public String getCurrentUserId() {
        return Amplify.Auth.getCurrentUser().getUserId();
    }

}
