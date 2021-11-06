package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;

public class ErrorActivity extends AppCompatActivity {

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        authController = AuthController.getInstance();
        authController.setErrorActivity(ErrorActivity.this);

    }
}