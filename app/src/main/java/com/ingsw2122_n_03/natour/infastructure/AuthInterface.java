package com.ingsw2122_n_03.natour.infastructure;

import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public interface AuthInterface {

     void configureAuth(BaseActivity callingActivity);
     boolean checkUserLogged();
     void login(String username, String password);
     void signUp(String username, String email, String password);
     void confirmSignUp(String username, String confirmationCode);
     void sendVerificationCode(String username);
     void loginWithGoogle(BaseActivity callingActivity);
     void signOut();

}
