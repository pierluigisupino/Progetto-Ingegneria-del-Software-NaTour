package com.ingsw2122_n_03.natour.infastructure;

import android.app.Activity;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public interface BackendInterface {
    public void setUp();
    public void checkUserLogged(BaseActivity callingActivity);
    public void login(BaseActivity callingActivity, String username, String password, LinearProgressIndicator progressBar);
    public void signUp(BaseActivity callingActivity, String username, String email, String password, LinearProgressIndicator progressBar);
    public void confirmSignUp(BaseActivity callingActivity, String username, String confirmationCode);
    public void sendVerificationCode(String username);
    public void loginWithGoogle(BaseActivity callingActivity);
    public void signOut(Activity callingActivity);
}
