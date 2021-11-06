package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class LoginActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;
    private LinearProgressIndicator progressBar;
    private MaterialToolbar materialToolbar;
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authController = AuthController.getInstance();
        authController.setLoginActivity(LoginActivity.this);

        layout = (ConstraintLayout) findViewById(R.id.layout);
        materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DA FARE CHECK INPUT (solo se ha inserito i campi, vengono controllati da amplify)
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                authController.login(username, password);
            }
        });

    }

    @Override
    public void onSuccess(String msg) {
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(LoginActivity.this, R.color.success))
                .show();
    }

    @Override
    public void onFail(String msg) {
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(LoginActivity.this, R.color.error))
                .show();
    }

}