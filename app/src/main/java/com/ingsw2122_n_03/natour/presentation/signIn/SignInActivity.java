package com.ingsw2122_n_03.natour.presentation.signIn;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.databinding.ActivitySignInBinding;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.util.Objects;

public class SignInActivity extends BaseActivity {

    private AuthController authController;

    private ConstraintLayout layout;

    private TextInputLayout  emailTextInputLayout;
    private TextInputEditText emailEditText;

    private TextInputLayout passwordTextInputLayout;
    private TextInputEditText passwordEditText;

    private Button signInButton;
    private LinearProgressIndicator progressBar;

    private boolean isFirstSubmit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ActivitySignInBinding binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        authController = AuthController.getInstance();
        authController.setSignInActivity(SignInActivity.this);

        layout = binding.layout;
        MaterialToolbar materialToolbar = binding.topAppBar;

        emailTextInputLayout = binding.emailTextInputLayout;
        emailEditText = binding.emailTextInputEditText;

        passwordTextInputLayout = binding.passwordTextInputLayout;
        passwordEditText = binding.passwordTextInputEditText;

        TextView forgotPasswordButton = binding.forgotPasswordButton;

        signInButton = binding.signInButton;
        progressBar = binding.progressBar;

        LottieAnimationView lottieAnimationView = binding.signInAnimation;
        lottieAnimationView.setMaxFrame(85);

        materialToolbar.setNavigationOnClickListener(v -> finish());

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

        passwordEditText.addTextChangedListener(new TextWatcher(){

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

        forgotPasswordButton.setOnClickListener(v ->
                authController.goToActivity(SignInActivity.this, ForgotPasswordActivity.class)
        );

        signInButton.setOnClickListener(v -> {

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(signInButton.getWindowToken(), 0);

            isFirstSubmit = false;

            if(isEmailValid() & isPasswordValid()) {
                String email = String.valueOf(emailEditText.getText());
                String password = String.valueOf(passwordEditText.getText());
                progressBar.setVisibility(View.VISIBLE);
                authController.signIn(email, password);
            }
        });

    }

    @Override
    public void onSuccess(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(SignInActivity.this, R.color.success));

            TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.euclid_circular_regular);
            tv.setTypeface(typeface);

            snackbar.show();
        });
    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> {
            progressBar.setVisibility(View.INVISIBLE);
            Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(SignInActivity.this, R.color.error));

            TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.euclid_circular_regular);
            tv.setTypeface(typeface);

            snackbar.show();
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

    private boolean isPasswordValid(){
        if(passwordEditText.getText() == null || passwordEditText.getText().length() == 0) {
            passwordTextInputLayout.setError(getString(R.string.password_warning));
            return false;
        }else if(passwordEditText.getText().length() < 8) {
            passwordTextInputLayout.setError(getString(R.string.password_length_error));
            return false;
        }
        passwordTextInputLayout.setError(null);
        return true;
    }
}