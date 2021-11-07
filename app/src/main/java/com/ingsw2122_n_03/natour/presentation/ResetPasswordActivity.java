package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class ResetPasswordActivity extends BaseActivity {

    private AuthController authController;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Intent intent = getIntent();
        String email = intent.getExtras().getString("email");

        authController = AuthController.getInstance();
        authController.setResetPasswordActivity(ResetPasswordActivity.this);

        layout = (ConstraintLayout) findViewById(R.id.layout);
        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);

        TextView emailTextView = (TextView) findViewById(R.id.emailTextView);
        emailTextView.setText(email);

        materialToolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    public void onSuccess(String snackbarMessage) {

    }

    @Override
    public void onFail(String snackbarMessage) {

    }
}