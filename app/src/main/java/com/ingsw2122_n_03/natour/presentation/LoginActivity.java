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

public class LoginActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;
    private LinearProgressIndicator progressBar;
    private MaterialToolbar materialToolbar;

    private TextInputLayout usernameTextInputLayout;
    private TextInputEditText usernameTextInputEditText;

    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordTextInputEditText;

    private Button loginButton;

    private boolean isFirstSubmit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authController = AuthController.getInstance();
        authController.setLoginActivity(LoginActivity.this);

        layout = (ConstraintLayout) findViewById(R.id.layout);
        materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);

        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        usernameTextInputEditText = findViewById(R.id.usernameTextInputEditText);

        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        passwordTextInputEditText = findViewById(R.id.passwordTextInputEditText);

        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirstSubmit = false;

                String username = usernameTextInputEditText.getText().toString();
                String password = passwordTextInputEditText.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                if(areInputValid(username, password)) {
                    authController.login(username, password);
                }
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

    private boolean areInputValid(String username, String password) {
        boolean isValid = isUsernameValid(username) & isPasswordValid(password);
        progressBar.setVisibility(View.INVISIBLE);
        return isValid;
    }

    private boolean isUsernameValid(String username){
        if(username == null || username.isEmpty()) {
            usernameTextInputLayout.setError("Please, insert a username!");
            return false;
        }

        usernameTextInputLayout.setError(null);
        return true;
    }

    private boolean isPasswordValid(String password){
        if(password == null || password.isEmpty()) {
            passwordTextInputLayout.setError("Please, insert a password!");
            return false;
        }

        passwordTextInputLayout.setError(null);
        return true;
    }

}