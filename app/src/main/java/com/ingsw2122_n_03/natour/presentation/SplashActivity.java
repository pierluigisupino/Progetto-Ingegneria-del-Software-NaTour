package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.Controller;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Controller controller = Controller.getInstance();

        Objects.requireNonNull(getSupportActionBar()).hide();

        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());

            if(controller.getUser()){
                finish();
                Log.e("NaTour", "Navigating to MainPage");
                //TO BE IMPLEMENTED
            }else{
                finish();
                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        } catch (AmplifyException e) {
            finish();
            Intent intent = new Intent(SplashActivity.this, ErrorActivity.class);
            startActivity(intent);
        }
    }
}