package com.ingsw2122_n_03.natour.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.databinding.ActivityResetPasswordBinding;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class ResetPasswordActivity extends BaseActivity {

    private AuthController authController;
    private ConstraintLayout layout;

    private TextInputLayout newPasswordTextInputLayout;
    private TextInputEditText newPasswordEditText;

    private PinView verificationCodePinView;

    private Button resetPasswordButton;
    private LinearProgressIndicator progressBar;

    private boolean isFirstSubmit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityResetPasswordBinding binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        String email = intent.getExtras().getString("email");

        authController = AuthController.getInstance();
        authController.setResetPasswordActivity(ResetPasswordActivity.this);

        layout = binding.layout;
        MaterialToolbar materialToolbar = binding.topAppBar;

        newPasswordTextInputLayout = binding.newPasswordTextInputLayout;
        newPasswordEditText = binding.newPasswordTextInputEditText;

        TextView emailTextView = binding.emailTextView;
        emailTextView.setText(email);

        verificationCodePinView = binding.verificationCode;

        resetPasswordButton = binding.resetButton;
        progressBar = binding.progressBar;

        materialToolbar.setNavigationOnClickListener(v -> finish());

        newPasswordEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isPasswordValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resetPasswordButton.setOnClickListener(v -> {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(resetPasswordButton.getWindowToken(), 0);

            isFirstSubmit = false;

            if(areInputValid()) {
                String newPassword = String.valueOf(newPasswordEditText.getText());
                String verificationCode = String.valueOf(verificationCodePinView.getText());
                progressBar.setVisibility(View.VISIBLE);
                authController.confirmResetPassword(newPassword, verificationCode);
            }
        });
    }

    @Override
    public void onSuccess(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(ResetPasswordActivity.this, R.color.success))
                    .show();
        });
    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(ResetPasswordActivity.this, R.color.error))
                    .show();
        });
    }

    private boolean areInputValid() {
        return isPasswordValid() & isVerificationCodeValid();
    }

    private boolean isPasswordValid(){
        if(newPasswordEditText.getText() == null || newPasswordEditText.getText().length() == 0) {
            newPasswordTextInputLayout.setError(getString(R.string.password_warning));
            return false;
        }else if(newPasswordEditText.getText().length() < 8) {
            newPasswordTextInputLayout.setError(getString(R.string.password_length_error));
            return false;
        }
        newPasswordTextInputLayout.setError(null);
        return true;
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