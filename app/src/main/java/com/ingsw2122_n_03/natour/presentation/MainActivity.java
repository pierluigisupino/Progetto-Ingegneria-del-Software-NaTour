package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.Controller;

public class MainActivity extends AppCompatActivity {

    private Controller startController;
    private Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startController = Controller.getInstance();

        signOutButton = findViewById(R.id.signOutButton);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startController.signOut(MainActivity.this);
            }
        });

    }
}