package com.ingsw2122_n_03.natour.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.chaos.view.PinView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class VerifyAccountActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;
    private PinView verificationCodePinView;

    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        Intent intent = getIntent();
        String email = intent.getExtras().getString("email");
        String password = intent.getExtras().getString("password");

        authController = AuthController.getInstance();
        authController.setVerifyAccountActivity(VerifyAccountActivity.this);

        layout = (ConstraintLayout) findViewById(R.id.layout);
        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);

        TextView emailTextView = (TextView) findViewById(R.id.emailTextView);
        emailTextView.setText(email);

        verificationCodePinView = (PinView) findViewById(R.id.verificationCode);

        TextView resendCodeTextView = (TextView) findViewById(R.id.resendCode);

        Button verifyButton = (Button) findViewById(R.id.verifyButton);

        progressBar = (LinearProgressIndicator) findViewById(R.id.progressBar);

        materialToolbar.setNavigationOnClickListener(view -> finish());

        resendCodeTextView.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            authController.sendVerificationCode(email);
        });

        verifyButton.setOnClickListener(view -> {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(verifyButton.getWindowToken(), 0);

            progressBar.setVisibility(View.VISIBLE);

            String verificationCode = verificationCodePinView.getText().toString();
            authController.confirmSignUp(email, password, verificationCode);
        });
    }

    @Override
    public void onSuccess(String msg) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(VerifyAccountActivity.this, R.color.success))
                    .show();
        });
    }

    @Override
    public void onFail(String msg) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(VerifyAccountActivity.this, R.color.error))
                    .show();
        });
    }
}