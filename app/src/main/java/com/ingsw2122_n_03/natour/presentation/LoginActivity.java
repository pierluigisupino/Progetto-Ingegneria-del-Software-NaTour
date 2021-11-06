package com.ingsw2122_n_03.natour.presentation;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class LoginActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;

    private TextInputLayout  emailTextInputLayout;
    private TextInputEditText  emailTextInputEditText;

    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordTextInputEditText;

    private Button loginButton;
    private LinearProgressIndicator progressBar;

    private boolean isFirstSubmit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authController = AuthController.getInstance();
        authController.setLoginActivity(LoginActivity.this);

        layout = (ConstraintLayout) findViewById(R.id.layout);
        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);

        emailTextInputLayout = (TextInputLayout) findViewById(R.id.emailTextInputLayout);
        emailTextInputEditText = (TextInputEditText) findViewById(R.id.emailTextInputEditText);

        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
        passwordTextInputEditText = (TextInputEditText) findViewById(R.id.passwordTextInputEditText);

        loginButton = (Button) findViewById(R.id.loginButton);
        progressBar = (LinearProgressIndicator) findViewById(R.id.progressBar);

        materialToolbar.setNavigationOnClickListener(view -> finish());

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

        loginButton.setOnClickListener(view -> {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(loginButton.getWindowToken(), 0);

            isFirstSubmit = false;

            String email =  emailTextInputEditText.getText().toString();
            String password = passwordTextInputEditText.getText().toString();

            if(areInputValid(email, password)) {
                progressBar.setVisibility(View.VISIBLE);
                authController.login(email, password);
            }
        });

    }

    @Override
    public void onSuccess(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(LoginActivity.this, R.color.success))
                    .show();
        });
    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(LoginActivity.this, R.color.error))
                    .show();
        });
    }

    private boolean areInputValid(String username, String password) {
        return isEmailValid(username) & isPasswordValid(password);
    }

    private boolean isEmailValid(String username){
        if(username == null || username.isEmpty()) {
            emailTextInputLayout.setError(getString(R.string.username_warning));
            return false;
        }

        emailTextInputLayout.setError(null);
        return true;
    }

    private boolean isPasswordValid(String password){
        if(password == null || password.isEmpty()) {
            passwordTextInputLayout.setError(getString(R.string.password_warning));
            return false;
        }

        passwordTextInputLayout.setError(null);
        return true;
    }

}