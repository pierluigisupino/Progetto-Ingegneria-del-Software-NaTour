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
import com.ingsw2122_n_03.natour.databinding.ActivityVerifyAccountBinding;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class VerifyAccountActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;
    private PinView verificationCodePinView;

    private LinearProgressIndicator progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityVerifyAccountBinding binding = ActivityVerifyAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        String email = intent.getExtras().getString("email");
        String password = intent.getExtras().getString("password");

        authController = AuthController.getInstance();
        authController.setVerifyAccountActivity(VerifyAccountActivity.this);

        layout = binding.layout;
        MaterialToolbar materialToolbar = binding.topAppBar;

        TextView emailTextView = binding.emailTextView;
        emailTextView.setText(email);

        verificationCodePinView = binding.verificationCode;

        TextView resendCodeTextView = binding.resendCode;

        Button verifyButton = binding.verifyButton;

        progressBar = binding.progressBar;

        materialToolbar.setNavigationOnClickListener(v -> finish());

        resendCodeTextView.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            authController.sendVerificationCode(email);
        });

        verifyButton.setOnClickListener(v -> {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(verifyButton.getWindowToken(), 0);

            if(isVerificationCodeValid()) {
                progressBar.setVisibility(View.VISIBLE);
                authController.confirmSignUp(email, password, String.valueOf(verificationCodePinView.getText()));
            }
        });
    }

    @Override
    public void onSuccess(String msg) {
        runOnUiThread(() -> Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(VerifyAccountActivity.this, R.color.success))
                .show());
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

    private boolean isVerificationCodeValid(){
        if(verificationCodePinView.getText() == null || verificationCodePinView.getText().length() == 0) {
            onFail(getString(R.string.verification_code_warning));
            return false;
        }else if(verificationCodePinView.getText().length() < 6) {
            onFail(getString(R.string.verification_code_warning_1));
            return false;
        }

        return true;
    }
}