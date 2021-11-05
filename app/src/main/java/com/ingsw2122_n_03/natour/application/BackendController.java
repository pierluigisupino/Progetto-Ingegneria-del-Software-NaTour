package com.ingsw2122_n_03.natour.application;

import android.app.Activity;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ingsw2122_n_03.natour.infastructure.BackendInterface;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class BackendController implements BackendInterface {

    private static BackendController instance = null;

    private BackendController() {}

    public static BackendController getInstance(){
        if(instance == null){
            instance = new BackendController();
        }
        return instance;
    }

    @Override
    public void setUp() {
        //CONFIGURA AMPLIFY
    }

    @Override
    public void checkUserLogged(BaseActivity callingActivity) {
        
    }

    @Override
    public void login(BaseActivity callingActivity, String username, String password, LinearProgressIndicator progressBar) {

    }

    @Override
    public void signUp(BaseActivity callingActivity, String username, String email, String password, LinearProgressIndicator progressBar) {

    }

    @Override
    public void confirmSignUp(BaseActivity callingActivity, String username, String confirmationCode) {

    }

    @Override
    public void sendVerificationCode(String username) {

    }

    @Override
    public void loginWithGoogle(BaseActivity callingActivity) {

    }

    @Override
    public void signOut(Activity callingActivity) {

    }
}
