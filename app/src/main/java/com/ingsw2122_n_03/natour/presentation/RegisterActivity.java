package com.ingsw2122_n_03.natour.presentation;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.application.Controller;
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
                //DA FARE CHECK INPUT (fare funzione booleana per sintassi (va bene in questa classe o nello StartController?) e funzione per verificare i campi con amplify (dove?))
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                authController.signUp(RegisterActivity.this, username, email, password, progressBar);
            }
        });

    }

    @Override
    public void onSuccess(String msg) {
        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(RegisterActivity.this, R.color.success))
                .show();
    }

    @Override
    public void onFail(String msg) {
        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(RegisterActivity.this, R.color.error))
                .show();
    }
}