package com.ingsw2122_n_03.natour.presentation;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.databinding.ActivityWelcomeBinding;
import com.ingsw2122_n_03.natour.presentation.signIn.SignInActivity;
import com.ingsw2122_n_03.natour.presentation.signUp.SignUpActivity;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class WelcomeActivity extends BaseActivity {

    private AuthController authController;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityWelcomeBinding binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        authController = AuthController.getInstance();
        authController.setWelcomeActivity(WelcomeActivity.this);

        layout = binding.layout;

        Button signUpButton = binding.signUpButton;
        Button signInButton = binding.signInButton;
        FloatingActionButton signInWithGoogle = binding.signInWithGoogle;

        signUpButton.setOnClickListener(v -> authController.goToActivity(WelcomeActivity.this, SignUpActivity.class));

        signInButton.setOnClickListener(v -> authController.goToActivity(WelcomeActivity.this, SignInActivity.class));

        signInWithGoogle.setOnClickListener(v -> authController.loginWithGoogle());
    }

    @Override
    public void onSuccess(String msg) {

        runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(WelcomeActivity.this, R.color.success));

            TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.euclid_circular_regular);
            tv.setTypeface(typeface);

            snackbar.show();
        });
    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(WelcomeActivity.this, R.color.error));

            TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.euclid_circular_regular);
            tv.setTypeface(typeface);

            snackbar.show();
        });
    }

}