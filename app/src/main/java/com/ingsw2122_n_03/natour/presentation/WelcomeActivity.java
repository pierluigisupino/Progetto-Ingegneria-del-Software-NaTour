package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.Controller;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Controller controller = Controller.getInstance();

        Button loginButton = (Button) findViewById(R.id.loginButton);
        FloatingActionButton loginWithGoogle = (FloatingActionButton) findViewById(R.id.loginWithGoogle);
        FloatingActionButton loginWithFacebook = (FloatingActionButton) findViewById(R.id.loginWithFacebook);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.loginWithGoogle(WelcomeActivity.this);
            }
        });

        loginWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.loginWithFacebook(WelcomeActivity.this);
            }
        });
    }
}