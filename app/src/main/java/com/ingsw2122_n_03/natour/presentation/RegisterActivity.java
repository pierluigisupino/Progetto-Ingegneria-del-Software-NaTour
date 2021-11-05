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
import com.google.android.material.textfield.TextInputLayout;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;


public class RegisterActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;
    private LinearProgressIndicator progressBar;
    private MaterialToolbar materialToolbar;

    private TextInputLayout usernameTextInputLayout;
    private TextInputEditText usernameTextInputEditText;

    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailTextInputEditText;

    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordTextInputEditText;

    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authController = AuthController.getInstance();

        layout = (ConstraintLayout) findViewById(R.id.layout);
        materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);

        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        usernameTextInputEditText = findViewById(R.id.usernameTextInputEditText);

        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        emailTextInputEditText = findViewById(R.id.emailTextInputEditText);

        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        passwordTextInputEditText = findViewById(R.id.passwordTextInputEditText);

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
                String username = usernameTextInputEditText.getText().toString();
                String email = emailTextInputEditText.getText().toString();
                String password = passwordTextInputEditText.getText().toString();
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

        boolean isValid = true;

        if(username == null || username.isEmpty()) {
            usernameTextInputLayout.setError("Please, insert a username!");
            isValid = false;
        }
        if(username != null && username.length() < 4) {
            usernameTextInputLayout.setError("Username must contain at least 4 letters!");
            isValid = false;
        }
        if(username != null && username.length() > 20) {
            usernameTextInputLayout.setError("Username must contain up to 20 letters!");
            isValid = false;
        }
        if(email == null || email.isEmpty()) {
            emailTextInputLayout.setError("Please, insert an e-mail address!");
            isValid = false;
        }
        if(email != null && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextInputLayout.setError("Please Check your e-mail address!");
            isValid = false;
        }
        if(password == null || password.isEmpty()) {
            passwordTextInputLayout.setError("Please, insert a password!");
            isValid = false;
        }
        if(password != null && password.length() < 6) {
            passwordTextInputLayout.setError("Password must contain at least 6 letters!");
            isValid = false;
        }

        progressBar.setVisibility(View.INVISIBLE);
        return isValid;
    }
}