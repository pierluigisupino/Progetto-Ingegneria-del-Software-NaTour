package com.ingsw2122_n_03.natour.presentation;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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


public class SignUpActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;
    private LinearProgressIndicator progressBar;

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
        setContentView(R.layout.activity_sign_up);

        authController = AuthController.getInstance();
        authController.setRegisterActivity(SignUpActivity.this);

        layout = findViewById(R.id.layout);
        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);

        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        usernameTextInputEditText = findViewById(R.id.usernameTextInputEditText);

        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        emailTextInputEditText = findViewById(R.id.emailTextInputEditText);

        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        passwordTextInputEditText = findViewById(R.id.passwordTextInputEditText);

        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        materialToolbar.setNavigationOnClickListener(view -> finish());

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


        registerButton.setOnClickListener(view -> {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);

            isFirstSubmit = false;

            String username = getText(usernameTextInputEditText);
            String email =  getText(emailTextInputEditText);
            String password = getText(passwordTextInputEditText);

            if(areInputValid(username, email, password)) {
                progressBar.setVisibility(View.VISIBLE);
                authController.signUp(username, email, password);
            }
        });

    }

    @Override
    public void onSuccess(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(SignUpActivity.this, R.color.success))
                    .show();
        });
    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(SignUpActivity.this, R.color.error))
                    .show();
        });
    }

    private boolean areInputValid(String username, String email, String password) {
        return isUsernameValid(username) & isEmailValid(email) & isPasswordValid(password);
    }

    private boolean isUsernameValid(String username){
        if(username == null || username.isEmpty()) {
            usernameTextInputLayout.setError(getString(R.string.username_warning));
            return false;
        }else if(username.length() < 4) {
            usernameTextInputLayout.setError(getString(R.string.username_length_error));
            return false;
        }
        usernameTextInputLayout.setError(null);
        return true;
    }

    private boolean isEmailValid(String email){
        if(email == null || email.isEmpty()) {
            emailTextInputLayout.setError(getString(R.string.email_warning));
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTextInputLayout.setError(getString(R.string.mail_error));
            return false;
        }
        emailTextInputLayout.setError(null);
        return true;
    }

    private boolean isPasswordValid(String password){
        if(password == null || password.isEmpty()) {
            passwordTextInputLayout.setError(getString(R.string.password_warning));
            return false;
        }else if(password.length() < 8) {
            passwordTextInputLayout.setError(getString(R.string.password_length_error));
            return false;
        }
        passwordTextInputLayout.setError(null);
        return true;
    }
}