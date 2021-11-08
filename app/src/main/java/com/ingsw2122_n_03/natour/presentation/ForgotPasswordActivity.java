package com.ingsw2122_n_03.natour.presentation;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.util.Objects;

public class ForgotPasswordActivity extends BaseActivity {

    private AuthController authController;
    private ConstraintLayout layout;

    private TextInputLayout emailTextInputLayout;
    private TextInputEditText emailEditText;

    private Button continueButton;
    private LinearProgressIndicator progressBar;

    private boolean isFirstSubmit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authController = AuthController.getInstance();
        authController.setForgotPasswordActivity(ForgotPasswordActivity.this);

        layout = findViewById(R.id.layout);
        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);

        emailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        emailEditText = findViewById(R.id.emailTextInputEditText);

        continueButton = findViewById(R.id.continue_button);
        progressBar = findViewById(R.id.progressBar);

        materialToolbar.setNavigationOnClickListener(view -> finish());

        emailEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isEmailValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        continueButton.setOnClickListener(view -> {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(continueButton.getWindowToken(), 0);

            isFirstSubmit = false;

            if(isEmailValid()) {
                progressBar.setVisibility(View.VISIBLE);
                authController.resetPassword(String.valueOf(emailEditText.getText()));
            }
        });

    }

    private boolean isEmailValid(){
        if(emailEditText.getText() == null || emailEditText.getText().length() == 0) {
            emailTextInputLayout.setError(getString(R.string.email_warning));
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(emailEditText.getText())).matches()) {
            emailTextInputLayout.setError(getString(R.string.mail_error));
            return false;
        }
        emailTextInputLayout.setError(null);
        return true;
    }

    @Override
    public void onSuccess(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(ForgotPasswordActivity.this, R.color.success))
                    .show();
        });
    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(ForgotPasswordActivity.this, R.color.error))
                    .show();
        });
    }
}