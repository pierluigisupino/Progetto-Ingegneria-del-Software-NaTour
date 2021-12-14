package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.databinding.ActivityErrorBinding;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityErrorBinding binding = ActivityErrorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        AuthController authController = AuthController.getInstance();
        authController.setErrorActivity(ErrorActivity.this);

    }
}