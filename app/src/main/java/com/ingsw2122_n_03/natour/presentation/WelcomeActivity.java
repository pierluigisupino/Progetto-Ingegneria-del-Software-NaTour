package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class WelcomeActivity extends BaseActivity {

    private AuthController authController;

    private Button registerButton;
    private Button loginButton;
    private FloatingActionButton googleLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        authController = AuthController.getInstance();
        authController.setWelcomeActivity(WelcomeActivity.this);

        registerButton = (Button) findViewById(R.id.registerButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        googleLoginButton = (FloatingActionButton) findViewById(R.id.loginWithGoogle);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authController.onRegisterButtonPressed();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authController.onLoginButtonPressed();
            }
        });

        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authController.loginWithGoogle();
            }
        });

    }

    @Override
    public void onSuccess(String msg) {
        //SHOW SNACKBAR
    }

    @Override
    public void onFail(String msg) {
        //SHOW SNACKBAR
    }
}