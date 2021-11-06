package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.Objects;


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

    private boolean isFirstSubmit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authController = AuthController.getInstance();
        authController.setRegisterActivity(RegisterActivity.this);

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

        usernameTextInputEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isUsernameValid(usernameTextInputEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailTextInputEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isEmailValid(emailTextInputEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

       passwordTextInputEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isPasswordValid(passwordTextInputEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirstSubmit = false;

                String username = usernameTextInputEditText.getText().toString();
                String email = emailTextInputEditText.getText().toString();
                String password = passwordTextInputEditText.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                if(areInputValid(username, email, password)) {
                    authController.signUp(username, email, password);
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
        boolean isValid = isUsernameValid(username) & isEmailValid(email) & isPasswordValid(password);
        progressBar.setVisibility(View.INVISIBLE);
        return isValid;
    }

    private boolean isUsernameValid(String username){
        if(username == null || username.isEmpty()) {
            usernameTextInputLayout.setError("Please, insert a username!");
            return false;
        }else if(username.length() < 4) {
            usernameTextInputLayout.setError("Username must contain at least 4 letters!");
            return false;
        }
        usernameTextInputLayout.setError(null);
        return true;
    }

    private boolean isEmailValid(String email){
        if(email == null || email.isEmpty()) {
            emailTextInputLayout.setError("Please, insert an e-mail address!");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextInputLayout.setError("Please Check your e-mail address!");
            return false;
        }
        emailTextInputLayout.setError(null);
        return true;
    }

    private boolean isPasswordValid(String password){
        if(password == null || password.isEmpty()) {
            passwordTextInputLayout.setError("Please, insert a password!");
            return false;
        }else if(password.length() < 8) {
            passwordTextInputLayout.setError("Password must contain at least 8 letters!");
            return false;
        }
        passwordTextInputLayout.setError(null);
        return true;
    }
}