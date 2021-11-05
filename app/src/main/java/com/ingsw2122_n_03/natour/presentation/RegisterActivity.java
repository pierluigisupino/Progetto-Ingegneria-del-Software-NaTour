package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;
import android.util.Patterns;
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


public class RegisterActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;
    private LinearProgressIndicator progressBar;
    private MaterialToolbar materialToolbar;
    private TextInputEditText usernameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authController = AuthController.getInstance();

        layout = (ConstraintLayout) findViewById(R.id.layout);
        materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        progressBar = (LinearProgressIndicator) findViewById(R.id.progressBar);


        materialToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                if(areInputValid(username, email, password)) {
                    authController.signUp(RegisterActivity.this, username, email, password);
                }
            }
        });

    }

    @Override
    public void onSuccess(String msg) {
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(RegisterActivity.this, R.color.success))
                .show();
    }

    @Override
    public void onFail(String msg) {
        progressBar.setVisibility(View.INVISIBLE);
        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(RegisterActivity.this, R.color.error))
                .show();
    }

    private boolean areInputValid(String username, String email, String password) {
        if(username == null || username.isEmpty()) {
            onFail("Please, insert a username!");
            return false;
        }
        if(username.length() < 4) {
            onFail("Username must contain at least 4 letters!");
            return false;
        }
        if(username.length() > 20) {
            onFail("Username must contain up to 20 letters!");
            return false;
        }
        if(email == null || email.isEmpty()) {
            onFail("Please, insert an e-mail address!");
            return false;
        }
        if(! Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onFail("Please Check your e-mail address!");
            return false;
        }
        if(password == null || password.isEmpty()) {
            onFail("Please, insert a password!");
            return false;
        }
        if(password.length() < 6) {
            onFail("Password must contain at least 6 letters!");
            return false;
        }
        return true;
    }
}