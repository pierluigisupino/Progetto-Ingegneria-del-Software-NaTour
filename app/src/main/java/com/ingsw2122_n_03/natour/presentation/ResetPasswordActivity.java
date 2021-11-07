package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;

public class ResetPasswordActivity extends AppCompatActivity {

    private AuthController authController;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        authController = AuthController.getInstance();
        authController.setResetPasswordActivity(ResetPasswordActivity.this);

        layout = (ConstraintLayout) findViewById(R.id.layout);
        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);

        materialToolbar.setNavigationOnClickListener(view -> finish());
    }
}