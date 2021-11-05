package com.ingsw2122_n_03.natour.application;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;

import com.ingsw2122_n_03.natour.presentation.MainActivity;
import com.ingsw2122_n_03.natour.presentation.VerifyAccount;
import com.ingsw2122_n_03.natour.presentation.WelcomeActivity;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.util.HashMap;
import java.util.Objects;

public class Controller {

    protected Controller() {}

    public void goToActivity(BaseActivity source, Class<?> destination){
        Intent intent = new Intent(source, destination);
        source.startActivity(intent);
    }

    public void goToActivity(BaseActivity source, Class<?> destination, HashMap<String, String> extras){
        Intent intent = new Intent(source, destination);

        for(String key : extras.keySet()){
            intent.putExtra(key, extras.get(key));
        }

        source.startActivity(intent);
    }

    public void goToActivityAndFinish(BaseActivity source, Class<?> destination){
        Intent intent = new Intent(source, destination);
        source.startActivity(intent);
        source.finish();
    }

    public void goToActivityAndFinish(BaseActivity source, Class<?> destination, HashMap<String, String> extras){
        Intent intent = new Intent(source, destination);

        for(String key : extras.keySet()){
            intent.putExtra(key, extras.get(key));
        }

        source.startActivity(intent);
        source.finish();
    }


}
