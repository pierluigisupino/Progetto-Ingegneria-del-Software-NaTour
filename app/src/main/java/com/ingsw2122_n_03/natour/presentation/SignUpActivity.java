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

import java.util.Objects;


public class SignUpActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;
    private LinearProgressIndicator progressBar;

    private TextInputLayout usernameTextInputLayout;
    private TextInputEditText usernameEditText;

    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailEditText;

    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordEditText;

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
        usernameEditText = findViewById(R.id.usernameTextInputEditText);

        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        emailEditText = findViewById(R.id.emailTextInputEditText);

        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        passwordEditText = findViewById(R.id.passwordTextInputEditText);

        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        materialToolbar.setNavigationOnClickListener(view -> finish());

        usernameEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isUsernameValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isEmailValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isPasswordValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        registerButton.setOnClickListener(view -> {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);

            isFirstSubmit = false;

            if(areInputValid()) {
                String username = String.valueOf(usernameEditText.getText());
                String email =  String.valueOf(emailEditText.getText());
                String password = String.valueOf(passwordEditText.getText());
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

    private boolean areInputValid() {
        return isUsernameValid() & isEmailValid() & isPasswordValid();
    }

    private boolean isUsernameValid(){
        if(usernameEditText.getText() == null || usernameEditText.getText().length() == 0) {
            usernameTextInputLayout.setError(getString(R.string.username_warning));
            return false;
        }else if(usernameEditText.getText().length() < 4) {
            usernameTextInputLayout.setError(getString(R.string.username_length_error));
            return false;
        }
        usernameTextInputLayout.setError(null);
        return true;
    }

    private boolean isEmailValid(){
        if(emailEditText.getText() == null || emailEditText.getText().length() == 0) {
            emailTextInputLayout.setError(getString(R.string.email_warning));
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(emailEditText.getText())).matches()) {
            emailTextInputLayout.setError(getString(R.string.mail_error));
            return false;
        }
        emailTextInputLayout.setError(null);
        return true;
    }

    private boolean isPasswordValid(){
        if(passwordEditText.getText() == null || passwordEditText.getText().length() == 0) {
            passwordTextInputLayout.setError(getString(R.string.password_warning));
            return false;
        }else if(passwordEditText.getText().length() < 8) {
            passwordTextInputLayout.setError(getString(R.string.password_length_error));
            return false;
        }
        passwordTextInputLayout.setError(null);
        return true;
    }
}