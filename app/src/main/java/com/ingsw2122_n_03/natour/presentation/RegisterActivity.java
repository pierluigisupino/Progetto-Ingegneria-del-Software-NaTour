package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.Controller;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;


public class RegisterActivity extends BaseActivity {

    private ConstraintLayout layout;
    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Controller controller = Controller.getInstance();

        layout = (ConstraintLayout) findViewById(R.id.layout);
        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        TextInputEditText usernameEditText = findViewById(R.id.username);
        TextInputEditText emailEditText = findViewById(R.id.email);
        TextInputEditText passwordEditText = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.registerButton);
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
                //DA FARE CHECK INPUT
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                controller.signUp(RegisterActivity.this, username, email, password, progressBar);
            }
        });
    }

    @Override
    public void onSuccess(String snackbarMessage) {
        Snackbar.make(layout, snackbarMessage, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(RegisterActivity.this, R.color.success))
                .show();
    }

    @Override
    public void onFail(String snackbarMessage) {
        Snackbar.make(layout, snackbarMessage, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(RegisterActivity.this, R.color.error))
                .show();
    }
}