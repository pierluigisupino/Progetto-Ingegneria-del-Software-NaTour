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

        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        FloatingActionButton signInWithGoogle = (FloatingActionButton) findViewById(R.id.sign_in_with_google);

        signUpButton.setOnClickListener(v -> authController.goToActivity(WelcomeActivity.this, SignUpActivity.class));

        signInButton.setOnClickListener(v -> authController.goToActivity(WelcomeActivity.this, SignInActivity.class));

        signInWithGoogle.setOnClickListener(v -> authController.loginWithGoogle());
    }

    @Override
    public void onSuccess(String msg) { }

    @Override
    public void onFail(String msg) { }
}