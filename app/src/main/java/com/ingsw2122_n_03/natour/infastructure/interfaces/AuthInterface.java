package com.ingsw2122_n_03.natour.infastructure.interfaces;

import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public interface AuthInterface {

     boolean configurePlugins(BaseActivity callingActivity);
     boolean checkUserLogged();
     void signIn(String email, String password);
     void signUp(String username, String email, String password);
     void confirmSignUp(String email, String password, String confirmationCode);
     void sendVerificationCode(String email);
     void loginWithGoogle(BaseActivity callingActivity);
     void resetPassword(String email);
     void confirmResetPassword(String newPassword, String confirmationCode);
     void signOut();

}
