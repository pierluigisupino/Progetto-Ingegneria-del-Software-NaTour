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
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class ResetPasswordActivity extends BaseActivity {

    private AuthController authController;
    private ConstraintLayout layout;

    private TextInputLayout newPasswordTextInputLayout;
    private TextInputEditText newPasswordTextInputEditText;

    private PinView verificationCodePinView;

    private Button resetPasswordButton;
    private LinearProgressIndicator progressBar;

    private boolean isFirstSubmit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Intent intent = getIntent();
        String email = intent.getExtras().getString("email");

        authController = AuthController.getInstance();
        authController.setResetPasswordActivity(ResetPasswordActivity.this);

        layout = findViewById(R.id.layout);
        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);

        newPasswordTextInputLayout = findViewById(R.id.newPasswordTextInputLayout);
        newPasswordTextInputEditText = findViewById(R.id.newPasswordTextInputEditText);

        TextView emailTextView = findViewById(R.id.emailTextView);
        emailTextView.setText(email);

        verificationCodePinView = findViewById(R.id.verificationCode);

        resetPasswordButton = findViewById(R.id.reset_button);
        progressBar = findViewById(R.id.progressBar);

        materialToolbar.setNavigationOnClickListener(view -> finish());

        newPasswordTextInputEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isPasswordValid(newPasswordTextInputEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        resetPasswordButton.setOnClickListener(view -> {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(resetPasswordButton.getWindowToken(), 0);

            isFirstSubmit = false;

            String newPassword = newPasswordTextInputEditText.getText().toString();
            String verificationCode = verificationCodePinView.getText().toString();

            if(isPasswordValid(newPassword)) {
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

    private boolean isPasswordValid(String password){
        if(password == null || password.isEmpty()) {
            newPasswordTextInputLayout.setError(getString(R.string.password_warning));
            return false;
        }

        newPasswordTextInputLayout.setError(null);
        return true;
    }
}