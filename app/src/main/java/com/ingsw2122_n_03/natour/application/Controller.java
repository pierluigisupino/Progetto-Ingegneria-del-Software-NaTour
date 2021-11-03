package com.ingsw2122_n_03.natour.application;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.domain.User;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;
import com.ingsw2122_n_03.natour.presentation.SplashActivity;
import com.ingsw2122_n_03.natour.presentation.WelcomeActivity;

public class Controller {

    private static Controller instance = null;

    private Controller() {}

    public static Controller getInstance(){
        if(instance == null){
            instance = new Controller();
        }
        return instance;
    }

    public boolean getUser() {
        return Amplify.Auth.getCurrentUser() != null;
    }

    public User loginWithGoogle(Activity callingActivity){
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), callingActivity,
                result -> Log.i("AuthQuickstart", result.toString()),
                error -> {
                    Intent intent = new Intent(callingActivity, ErrorActivity.class);
                    callingActivity.startActivity(intent);
                }
        );

        //DA COMPLETARE
        return new User();
    }

    public User loginWithFacebook(Activity callingActivity){
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.facebook(), callingActivity,
                result -> Log.i("AuthQuickstart", result.toString()),
                error -> {
                    Intent intent = new Intent(callingActivity, ErrorActivity.class);
                    callingActivity.startActivity(intent);
                }
        );

        //DA COMPLETARE
        return new User();
    }
}
