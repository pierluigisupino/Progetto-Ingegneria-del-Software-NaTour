package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;

public class MainActivity extends AppCompatActivity {

    private AuthController authController;
    private Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authController = AuthController.getInstance();

        signOutButton = findViewById(R.id.signOutButton);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authController.signOut(MainActivity.this);
            }
        });

    }
}