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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        authController = AuthController.getInstance();
        authController.setWelcomeActivity(WelcomeActivity.this);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        FloatingActionButton googleLoginButton = (FloatingActionButton) findViewById(R.id.loginWithGoogle);

        registerButton.setOnClickListener(v -> authController.onRegisterButtonPressed());

        loginButton.setOnClickListener(v -> authController.onLoginButtonPressed());

        googleLoginButton.setOnClickListener(v -> authController.loginWithGoogle());

    }

    @Override
    public void onSuccess(String msg) { }

    @Override
    public void onFail(String msg) { }
}